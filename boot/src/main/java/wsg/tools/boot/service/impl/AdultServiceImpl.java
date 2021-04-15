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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import wsg.tools.boot.pojo.entity.base.FailureReason;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.pojo.result.BatchResult;
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
        Optional<String> last = jaVideoRepository.getFirstOrderUpdateTime(domain, subtype);
        LinkedRepoIterator<String, T> iterator;
        if (last.isPresent()) {
            iterator = repository.linkedRepoIterator(last.get());
            try {
                iterator.next();
            } catch (NotFoundException e) {
                throw new AppException(e);
            }
        } else {
            iterator = repository.linkedRepoIterator();
        }
        BatchResult<String> result = BatchResult.empty();
        while (iterator.hasNext()) {
            T item = null;
            try {
                item = iterator.next();
            } catch (NotFoundException e) {
                throw new AppException(e);
            }
            Source source = Source.record(domain, subtype, 0, item);
            Optional<FailureReason> reason = saveJaAdultEntry(item, source);
            if (reason.isPresent()) {
                result.fail(item.getSerialNum(), reason.get().getText());
            } else {
                result.succeed();
            }
        }
        log.info("Imported adult entries from {}:", domain);
        result.print(Function.identity());
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
            Optional<Pair<String, Object>> merge = fail(exists, entity);
            if (merge.isPresent()) {
                Pair<String, Object> pair = merge.get();
                FailureReason reason = FailureReason.ARG_INVALID;
                String name = pair.getLeft();
                String arg = pair.getRight().toString();
                failureRepository.insert(new Failure(source, reason, fsn, name, arg));
                return Optional.of(reason);
            }

            // merge the two entities
            List<String> existsImages = exists.getImages();
            if (existsImages != null) {
                if (entity.getImages() == null) {
                    entity.setImages(existsImages);
                } else {
                    entity.getImages().addAll(existsImages);
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
            return Optional.empty();
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
            entity.setImages(config.uploadAlbum(images, source));
        } catch (NotFoundException | NotFiletypeException ignored) {
        } catch (OtherResponseException e) {
            throw new AppException(e);
        }
        return entity;
    }

    private Optional<Pair<String, Object>> fail(JaAdultVideoEntity old, JaAdultVideoEntity add) {
        return Optional.<Pair<String, Object>>empty()
            .or(fail(old, add, "title", JaAdultVideoEntity::getTitle, JaAdultVideoEntity::setTitle))
            .or(fail(old, add, "cover", JaAdultVideoEntity::getCover, JaAdultVideoEntity::setCover))
            .or(fail(old, add, "mosaic", JaAdultVideoEntity::getMosaic,
                JaAdultVideoEntity::setMosaic))
            .or(fail(old, add, "duration", JaAdultVideoEntity::getDuration,
                JaAdultVideoEntity::setDuration))
            .or(fail(old, add, "release", JaAdultVideoEntity::getRelease,
                JaAdultVideoEntity::setPublish))
            .or(fail(old, add, "producer", JaAdultVideoEntity::getProducer,
                JaAdultVideoEntity::setProducer))
            .or(fail(old, add, "distributor", JaAdultVideoEntity::getDistributor,
                JaAdultVideoEntity::setDistributor))
            .or(fail(old, add, "series", JaAdultVideoEntity::getSeries,
                JaAdultVideoEntity::setSeries));
    }

    /**
     * Returns the pair of property name and newer value if the two values of the specific property
     * are both not null and not equivalent. Otherwise, return {@code Optional#empty()} and merge
     * the non-null value, if exists, to the newer entity.
     */
    private <T> Supplier<Optional<Pair<String, Object>>> fail(JaAdultVideoEntity exists,
        JaAdultVideoEntity newEntity, String name, @Nonnull Function<JaAdultVideoEntity, T> getter,
        @Nonnull BiConsumer<JaAdultVideoEntity, T> setter) {
        T existsValue = getter.apply(exists);
        if (existsValue == null) {
            return Optional::empty;
        }
        T newValue = getter.apply(newEntity);
        if (newValue == null) {
            setter.accept(exists, existsValue);
            return Optional::empty;
        }
        if (newValue.equals(existsValue)) {
            return Optional::empty;
        }
        return () -> Optional.of(Pair.of(name, newValue));
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
