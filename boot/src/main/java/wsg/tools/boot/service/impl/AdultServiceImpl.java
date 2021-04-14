package wsg.tools.boot.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.config.MinioConfig;
import wsg.tools.boot.dao.jpa.mapper.FailureRepository;
import wsg.tools.boot.dao.jpa.mapper.JaAdultTagRepository;
import wsg.tools.boot.dao.jpa.mapper.JaAdultVideoRepository;
import wsg.tools.boot.dao.jpa.mapper.WesternAdultVideoRepository;
import wsg.tools.boot.pojo.entity.adult.JaAdultTagEntity;
import wsg.tools.boot.pojo.entity.adult.JaAdultVideoEntity;
import wsg.tools.boot.pojo.entity.adult.WesternAdultVideoEntity;
import wsg.tools.boot.pojo.entity.base.Failure;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.common.io.NotFiletypeException;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.repository.LinkedRepoIterator;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UpdateTemporalSupplier;
import wsg.tools.internet.info.adult.common.SerialNumber;
import wsg.tools.internet.info.adult.view.AlbumSupplier;
import wsg.tools.internet.info.adult.view.AmateurJaAdultEntry;
import wsg.tools.internet.info.adult.view.Classified;
import wsg.tools.internet.info.adult.view.DurationSupplier;
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
    private final WesternAdultVideoRepository wtVideoRepository;
    private final FailureRepository failureRepository;
    private final TransactionTemplate template;
    private final MinioConfig config;

    @Autowired
    public AdultServiceImpl(JaAdultVideoRepository jaVideoRepository,
        JaAdultTagRepository jaTagRepository, WesternAdultVideoRepository wtVideoRepository,
        FailureRepository failureRepository,
        TransactionTemplate template, MinioConfig config) {
        this.jaVideoRepository = jaVideoRepository;
        this.jaTagRepository = jaTagRepository;
        this.wtVideoRepository = wtVideoRepository;
        this.failureRepository = failureRepository;
        this.template = template;
        this.config = config;
    }

    @Override
    public <T extends AmateurJaAdultEntry & UpdateTemporalSupplier<?>>
    void importLinkedRepository(String domain, int subtype, LinkedRepository<String, T> repository)
        throws OtherResponseException {
        Optional<String> optional = jaVideoRepository.getFirstOrderUpdateTime(domain, subtype);
        LinkedRepoIterator<String, T> iterator;
        if (optional.isPresent()) {
            iterator = repository.linkedRepoIterator(optional.get());
            try {
                iterator.next();
            } catch (NotFoundException e) {
                throw new AppException(e);
            }
        } else {
            iterator = repository.linkedRepoIterator();
        }
        int success = 0, total = 0;
        while (iterator.hasNext()) {
            T item = null;
            try {
                item = iterator.next();
            } catch (NotFoundException e) {
                throw new AppException(e);
            }
            Source source = Source.record(domain, subtype, 0, item);
            success += saveJaAdultEntry(item, source);
            total++;
        }
        log.info("Imported adult entries from {}: {} succeed, {} failed", domain, success,
            total - success);
    }

    @Override
    public int saveJaAdultEntry(@Nonnull JaAdultEntry entry, Source source) {
        String serialNum = entry.getSerialNum();
        if (serialNum != null) {
            try {
                serialNum = SerialNumber.format(serialNum);
            } catch (IllegalArgumentException e) {
                String message = "The serial number is invalid: " + serialNum;
                failureRepository.insert(new Failure(source, message));
                return 0;
            }
        }
        JaAdultVideoEntity entity = copyEntry(entry, source);
        entity.setSource(source);
        if (serialNum == null) {
            jaVideoRepository.insert(entity);
            return 1;
        }

        String fsn = serialNum;
        return Objects.requireNonNull(template.execute(status -> {
            Optional<JaAdultVideoEntity> optional = jaVideoRepository.findBySerialNum(fsn);
            if (optional.isEmpty()) {
                jaVideoRepository.insert(entity);
                return 1;
            }
            JaAdultVideoEntity exists = optional.get();

            // if duplicate in the same site
            if (exists.getSource().getDomain().equals(entity.getSource().getDomain())) {
                return 0;
            }

            // if any property has conflict values
            if (merge(exists, entity)) {
                String message = "The target serial number exists: " + fsn;
                failureRepository.insert(new Failure(source, message));
                return 0;
            }

            // merge the two entities
            if (exists.getImages() != null) {
                if (entity.getImages() == null) {
                    entity.setImages(exists.getImages());
                } else {
                    entity.getImages().addAll(exists.getImages());
                }
            }
            Set<JaAdultTagEntity> tags = new HashSet<>();
            if (exists.getTags() != null) {
                tags.addAll(exists.getTags());
            }
            if (entity.getTags() != null) {
                tags.addAll(entity.getTags());
            }
            entity.setTags(tags);
            entity.setId(exists.getId());
            jaVideoRepository.updateById(entity);
            return 1;
        }));
    }

    private JaAdultVideoEntity copyEntry(JaAdultEntry entry, Source source) {
        JaAdultVideoEntity entity = new JaAdultVideoEntity();
        entity.setSerialNum(entry.getSerialNum());
        if (entry instanceof TitledAdultEntry) {
            entity.setTitle(((TitleSupplier) entry).getTitle());
        }
        if (entry instanceof CoverSupplier) {
            try {
                entity.setCover(config.uploadCover((CoverSupplier) entry, source));
            } catch (NotFoundException | NotFiletypeException ignored) {
            } catch (OtherResponseException e) {
                throw new AppException(e);
            }
        }
        entity.setMosaic(entry.getMosaic());
        if (entry instanceof DurationSupplier) {
            entity.setDuration(((DurationSupplier) entry).getDuration());
        }
        entity.setRelease(entry.getRelease());
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
            entity.setImages(config.uploadAlbum(images, source));
        } catch (NotFoundException | NotFiletypeException ignored) {
        } catch (OtherResponseException e) {
            throw new AppException(e);
        }
        return entity;
    }

    private boolean merge(JaAdultVideoEntity old, JaAdultVideoEntity add) {
        return merge(old, add, JaAdultVideoEntity::getTitle, JaAdultVideoEntity::setTitle)
            || merge(old, add, JaAdultVideoEntity::getCover, JaAdultVideoEntity::setCover)
            || merge(old, add, JaAdultVideoEntity::getMosaic, JaAdultVideoEntity::setMosaic)
            || merge(old, add, JaAdultVideoEntity::getDuration, JaAdultVideoEntity::setDuration)
            || merge(old, add, JaAdultVideoEntity::getRelease, JaAdultVideoEntity::setRelease)
            || merge(old, add, JaAdultVideoEntity::getProducer, JaAdultVideoEntity::setProducer)
            || merge(old, add, JaAdultVideoEntity::getDistributor,
            JaAdultVideoEntity::setDistributor)
            || merge(old, add, JaAdultVideoEntity::getSeries, JaAdultVideoEntity::setSeries);
    }

    /**
     * Returns {@code true} if the two values of the specific property are both not null and not
     * equivalent. Otherwise, return {@code false} and merge the non-null value, if exists, to the
     * new entity.
     */
    private <T> boolean merge(JaAdultVideoEntity exists, JaAdultVideoEntity newEntity,
        @Nonnull Function<JaAdultVideoEntity, T> getter,
        @Nonnull BiConsumer<JaAdultVideoEntity, T> setter) {
        T existsValue = getter.apply(exists);
        if (existsValue == null) {
            return false;
        }
        T newValue = getter.apply(newEntity);
        if (newValue == null) {
            setter.accept(exists, existsValue);
            return false;
        }
        return !newValue.equals(existsValue);
    }

    @Override
    public <T extends IntIdentifier & WesternAdultEntry>
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
                Source source = Source.record(domain, subtype, item);
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
        } catch (NotFoundException | NotFiletypeException ignored) {
        } catch (OtherResponseException e) {
            throw new AppException(e);
        }
        if (entry instanceof PreviewSupplier) {
            try {
                entity.setPreview(config.uploadPreview((PreviewSupplier) entry, source));
            } catch (NotFoundException | NotFiletypeException ignored) {
            } catch (OtherResponseException e) {
                throw new AppException(e);
            }
        }
        entity.setDuration(entry.getDuration());
        try {
            entity.setVideo(config.uploadVideo(entry, source));
        } catch (NotFoundException | NotFiletypeException ignored) {
        } catch (OtherResponseException e) {
            throw new AppException(e);
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
