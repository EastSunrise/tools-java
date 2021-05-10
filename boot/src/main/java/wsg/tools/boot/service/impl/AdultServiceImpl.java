package wsg.tools.boot.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.common.util.BeanOpt;
import wsg.tools.boot.common.util.BeanUtilExt;
import wsg.tools.boot.config.MinioConfig;
import wsg.tools.boot.dao.api.adapter.JaAdultActressAdapter;
import wsg.tools.boot.dao.api.adapter.JaAdultEntryAdapter;
import wsg.tools.boot.dao.api.adapter.WestAdultEntryAdapter;
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
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.info.adult.common.SerialNumber;

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
    public Optional<FailureReason> saveJaAdultEntry(@Nonnull JaAdultEntryAdapter adapter,
        Source source) {
        String serialNum = adapter.getSerialNum();
        if (serialNum != null) {
            try {
                serialNum = SerialNumber.format(serialNum);
            } catch (IllegalArgumentException e) {
                FailureReason reason = FailureReason.ARG_INVALID;
                failureRepository.insert(new Failure(source, reason, "serial number", serialNum));
                return Optional.of(reason);
            }
        }
        JaAdultVideoEntity entity = adapt(adapter, source);
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
            if (exists.getSource().getSname().equals(entity.getSource().getSname())) {
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

    private JaAdultVideoEntity adapt(JaAdultEntryAdapter adapter, Source source) {
        JaAdultVideoEntity entity = new JaAdultVideoEntity();
        entity.setTitle(adapter.getTitle());
        try {
            entity.setCover(config.uploadCover(adapter, source));
        } catch (NotFoundException ignored) {
        }
        entity.setMosaic(adapter.getMosaic());
        entity.setDuration(adapter.getDuration());
        entity.setPublish(adapter.getPublish());
        entity.setProducer(adapter.getProducer());
        entity.setDistributor(adapter.getDistributor());
        entity.setSeries(adapter.getSeries());
        Set<JaAdultTagEntity> tagEntities = adapter.getTags().stream()
            .map(tag -> {
                JaAdultTagEntity tagEntity = new JaAdultTagEntity();
                tagEntity.setTag(tag);
                return tagEntity;
            }).collect(Collectors.toSet());
        entity.setTags(new HashSet<>(jaTagRepository.saveAll(tagEntities)));
        try {
            entity.setImages(new HashSet<>(config.uploadAlbum(adapter.getImages(), source)));
        } catch (NotFoundException ignored) {
        }
        List<String> names = adapter.getActresses();
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
            actress.setImages(new HashSet<>(config.uploadAlbum(adapter.getImages(), source)));
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
    public Optional<FailureReason>
    saveWesternAdultEntry(@Nonnull WestAdultEntryAdapter adapter, Source source) {
        WesternAdultVideoEntity entity = new WesternAdultVideoEntity();
        entity.setTitle(adapter.getTitle());
        try {
            entity.setCover(config.uploadCover(adapter, source));
        } catch (NotFoundException ignored) {
        }
        entity.setDuration(adapter.getDuration());
        try {
            entity.setVideo(config.uploadVideo(adapter, source));
        } catch (NotFoundException ignored) {
        }
        entity.setTags(new ArrayList<>(adapter.getTags()));
        entity.setCategories(new ArrayList<>(adapter.getCategories()));
        entity.setDescription(adapter.getDescription());
        entity.setSource(source);
        try {
            entity.setImages(new HashSet<>(config.uploadAlbum(adapter.getImages(), source)));
        } catch (NotFoundException ignored) {
        }
        wtVideoRepository.insert(entity);
        return Optional.empty();
    }
}
