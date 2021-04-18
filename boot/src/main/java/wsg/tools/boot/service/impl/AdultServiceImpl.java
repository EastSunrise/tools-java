package wsg.tools.boot.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.common.util.BeanOpt;
import wsg.tools.boot.common.util.BeanUtilExt;
import wsg.tools.boot.config.MinioConfig;
import wsg.tools.boot.dao.api.adapter.JaAdultActressAdapter;
import wsg.tools.boot.dao.jpa.mapper.FailureRepository;
import wsg.tools.boot.dao.jpa.mapper.JaAdultActressRepository;
import wsg.tools.boot.dao.jpa.mapper.JaAdultTagRepository;
import wsg.tools.boot.dao.jpa.mapper.JaAdultVideoRepository;
import wsg.tools.boot.dao.jpa.mapper.WesternAdultVideoRepository;
import wsg.tools.boot.pojo.entity.adult.JaAdultActressEntity;
import wsg.tools.boot.pojo.entity.adult.JaAdultTagEntity;
import wsg.tools.boot.pojo.entity.adult.JaAdultVideoEntity;
import wsg.tools.boot.pojo.entity.adult.WesternAdultVideoEntity;
import wsg.tools.boot.pojo.entity.base.Failure;
import wsg.tools.boot.pojo.entity.base.FailureReason;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UpdateTemporalSupplier;
import wsg.tools.internet.info.adult.common.SerialNumber;
import wsg.tools.internet.info.adult.view.ActressSupplier;
import wsg.tools.internet.info.adult.view.AlbumSupplier;
import wsg.tools.internet.info.adult.view.Classified;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.FormalJaAdultEntry;
import wsg.tools.internet.info.adult.view.ImageSupplier;
import wsg.tools.internet.info.adult.view.JaAdultEntry;
import wsg.tools.internet.info.adult.view.PreviewSupplier;
import wsg.tools.internet.info.adult.view.Tagged;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;
import wsg.tools.internet.info.adult.west.WesternAdultEntry;

/**
 * @author Kingen
 * @see AdultService
 * @since 2021/3/5
 */
@Slf4j
@Service
public class AdultServiceImpl extends BaseServiceImpl implements AdultService {

    private final JaAdultVideoRepository jaVideoRepository;
    private final JaAdultTagRepository jaTagRepository;
    private final JaAdultActressRepository actressRepository;
    private final WesternAdultVideoRepository wtVideoRepository;
    private final FailureRepository failureRepository;
    private final TransactionTemplate template;
    private final MinioConfig config;

    @Autowired
    public AdultServiceImpl(JaAdultVideoRepository jaVideoRepository,
        JaAdultTagRepository jaTagRepository, JaAdultActressRepository actressRepository,
        WesternAdultVideoRepository wtVideoRepository, FailureRepository failureRepository,
        TransactionTemplate template, MinioConfig config) {
        this.jaVideoRepository = jaVideoRepository;
        this.jaTagRepository = jaTagRepository;
        this.actressRepository = actressRepository;
        this.wtVideoRepository = wtVideoRepository;
        this.failureRepository = failureRepository;
        this.template = template;
        this.config = config;
    }

    @Override
    public Optional<FailureReason> saveJaAdultEntry(@Nonnull JaAdultEntry entry, Source source) {
        String serialNum = entry.getSerialNum();
        if (serialNum != null) {
            try {
                serialNum = SerialNumber.format(serialNum);
            } catch (IllegalArgumentException e) {
                FailureReason reason = FailureReason.ARG_INVALID;
                failureRepository.insert(new Failure(source, reason, "serial number", serialNum));
                return Optional.of(reason);
            }
        }
        JaAdultVideoEntity entity = copyEntry(entry, source);
        entity.setSource(source);
        if (serialNum == null) {
            jaVideoRepository.insert(entity);
            return Optional.empty();
        }

        entity.setSerialNum(serialNum);
        String fsn = serialNum;
        return Objects.requireNonNull(template.execute(status -> {
            Optional<JaAdultVideoEntity> optional = jaVideoRepository.findBySerialNum(fsn);
            if (optional.isEmpty()) {
                jaVideoRepository.insert(entity);
                return Optional.empty();
            }
            JaAdultVideoEntity exists = optional.get();

            // if duplicate in the same site
            if (exists.getSource().getDomain().equals(entity.getSource().getDomain())) {
                return Optional.of(FailureReason.EXISTS);
            }

            // if any property has conflict values
            BeanOpt<JaAdultVideoEntity> opt = BeanUtilExt.getBeanOpt(JaAdultVideoEntity.class);
            Optional<String> propOp;
            try {
                propOp = opt.merge(exists, entity, new String[]{
                    "id", "title", "cover", "mosaic", "duration", "publish", "producer",
                    "distributor", "series"
                });
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new AppException(e);
            }
            if (propOp.isPresent()) {
                FailureReason reason = FailureReason.ARG_INVALID;
                failureRepository.insert(new Failure(source, reason, fsn, propOp.get()));
                return Optional.of(reason);
            }

            // merge left properties of the two entities
            entity.getImages().addAll(exists.getImages());
            entity.getTags().addAll(exists.getTags());
            entity.getActresses().addAll(exists.getActresses());
            jaVideoRepository.updateById(entity);
            return Optional.empty();
        }));
    }

    private JaAdultVideoEntity copyEntry(JaAdultEntry entry, Source source) {
        JaAdultVideoEntity entity = new JaAdultVideoEntity();
        if (entry instanceof TitledAdultEntry) {
            entity.setTitle(((TitleSupplier) entry).getTitle());
        }
        if (entry instanceof CoverSupplier) {
            try {
                entity.setCover(config.uploadCover((CoverSupplier) entry, source));
            } catch (NotFoundException ignored) {
            }
        }
        entity.setMosaic(entry.getMosaic());
        if (entry instanceof DurationSupplier) {
            entity.setDuration(((DurationSupplier) entry).getDuration());
        }
        entity.setPublish(entry.getRelease());
        entity.setProducer(entry.getProducer());
        entity.setDistributor(entry.getDistributor());
        entity.setSeries(entry.getSeries());
        if (entry instanceof Tagged) {
            Set<JaAdultTagEntity> tagEntities = Arrays.stream(((Tagged) entry).getTags())
                .distinct().map(tag -> {
                    JaAdultTagEntity tagEntity = new JaAdultTagEntity();
                    tagEntity.setTag(tag);
                    return tagEntity;
                }).collect(Collectors.toSet());
            entity.setTags(new HashSet<>(jaTagRepository.saveAll(tagEntities)));
        }
        List<URL> images = new ArrayList<>();
        if (entry instanceof ImageSupplier) {
            CollectionUtils.addIgnoreNull(images, ((ImageSupplier) entry).getImageURL());
        }
        if (entry instanceof AlbumSupplier) {
            images.addAll(((AlbumSupplier) entry).getAlbum());
        }
        try {
            entity.setImages(new HashSet<>(config.uploadAlbum(images, source)));
        } catch (NotFoundException ignored) {
        }
        if (entry instanceof FormalJaAdultEntry) {
            List<String> names = ((ActressSupplier) entry).getActresses();
            List<JaAdultActressEntity> actresses = new ArrayList<>(names.size());
            for (String name : names) {
                Optional<JaAdultActressEntity> actressOp = actressRepository.findByName(name);
                if (actressOp.isPresent()) {
                    actresses.add(actressOp.get());
                    continue;
                }
                JaAdultActressEntity actressEntity = new JaAdultActressEntity();
                actressEntity.setName(name);
                actresses.add(actressRepository.save(actressEntity));
            }
            entity.setActresses(new HashSet<>(actresses));
        }
        return entity;
    }

    @Override
    public Optional<FailureReason>
    saveJaAdultActress(@Nonnull JaAdultActressAdapter adapter, @Nonnull Source source) {
        JaAdultActressEntity actress = adapter.toActress();
        String name = actress.getName();
        if (name == null) {
            FailureReason reason = FailureReason.KEY_LACKING;
            failureRepository.insert(new Failure(source, reason, "name"));
            return Optional.of(reason);
        }
        try {
            actress.setImages(new HashSet<>(config.uploadAlbum(adapter.getAlbum(), source)));
        } catch (NotFoundException ignored) {
        }
        Optional<JaAdultActressEntity> entityOp = actressRepository.findByName(name);
        if (entityOp.isEmpty()) {
            actressRepository.insert(actress);
            return Optional.empty();
        }
        JaAdultActressEntity exists = entityOp.get();
        BeanOpt<JaAdultActressEntity> opt = BeanUtilExt.getBeanOpt(JaAdultActressEntity.class);
        Optional<String> propOp;
        try {
            propOp = opt.merge(exists, actress, new String[]{
                "id", "name", "zhName", "enName", "height", "weight", "cup", "birthday",
                "startDate", "retireDate", "region"
            });
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new AppException(e);
        }
        if (propOp.isPresent()) {
            FailureReason reason = FailureReason.ARG_INVALID;
            failureRepository.insert(new Failure(source, reason, name, propOp.get()));
            return Optional.of(reason);
        }
        actress.getAka().addAll(exists.getAka());
        actress.getImages().addAll(exists.getImages());
        actressRepository.updateById(actress);
        return Optional.empty();
    }

    @Override
    public <T extends IntIdentifier & WesternAdultEntry & UpdateTemporalSupplier<?>>
    void importIntListRepository(String domain, int subtype,
        @Nonnull ListRepository<Integer, T> repository)
        throws OtherResponseException {
        long start = wtVideoRepository.getMaxRid(domain, subtype).orElse(0L);
        List<Integer> indices = repository.indices().stream()
            .filter(id -> id > start).sorted().collect(Collectors.toList());
        int success = 0;
        for (int id : indices) {
            try {
                T item = repository.findById(id);
                Source source = Source.of(domain, subtype, item.getId(), item);
                success += saveWesternAdultEntry(item, source);
            } catch (NotFoundException ignored) {
            }
        }
        log.info("Imported adult entries from {}: {} succeed, {} failed", domain, success,
            indices.size() - success);
    }

    @Override
    public int saveWesternAdultEntry(@Nonnull WesternAdultEntry entry, Source source) {
        WesternAdultVideoEntity entity = new WesternAdultVideoEntity();
        entity.setTitle(entry.getTitle());
        try {
            entity.setCover(config.uploadCover(entry, source));
        } catch (NotFoundException ignored) {
        }
        if (entry instanceof PreviewSupplier) {
            try {
                entity.setPreview(config.uploadPreview((PreviewSupplier) entry, source));
            } catch (NotFoundException ignored) {
            }
        }
        entity.setDuration(entry.getDuration());
        try {
            entity.setVideo(config.uploadVideo(entry, source));
        } catch (NotFoundException ignored) {
        }
        entity.setTags(entry.getTags());
        if (entry instanceof Classified) {
            entity.setCategories(((Classified) entry).getCategories());
        }
        entity.setSource(source);
        wtVideoRepository.insert(entity);
        return 1;
    }
}
