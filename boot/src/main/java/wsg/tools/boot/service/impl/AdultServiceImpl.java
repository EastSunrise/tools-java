package wsg.tools.boot.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import wsg.tools.boot.config.MinioConfig;
import wsg.tools.boot.dao.jpa.mapper.AdultVideoRepository;
import wsg.tools.boot.dao.jpa.mapper.FailureRepository;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity;
import wsg.tools.boot.pojo.entity.adult.ImagePreview;
import wsg.tools.boot.pojo.entity.base.Failure;
import wsg.tools.boot.pojo.entity.base.IdView;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.common.io.NotFiletypeException;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.page.PageReq;
import wsg.tools.internet.base.page.PageResult;
import wsg.tools.internet.base.repository.LinkedRepoIterator;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.UpdateDateSupplier;
import wsg.tools.internet.base.view.UpdateDatetimeSupplier;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.info.adult.common.SerialNumber;
import wsg.tools.internet.info.adult.view.AlbumSupplier;
import wsg.tools.internet.info.adult.view.AmateurJaAdultEntry;
import wsg.tools.internet.info.adult.view.Tagged;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;

/**
 * @author Kingen
 * @see AdultService
 * @since 2021/3/5
 */
@Slf4j
@Service
public class AdultServiceImpl extends BaseServiceImpl implements AdultService {

    private static final LocalDateTime START = LocalDateTime.MIN;

    private final AdultVideoRepository videoRepository;
    private final FailureRepository failureRepository;
    private final MinioConfig config;

    @Autowired
    public AdultServiceImpl(AdultVideoRepository videoRepository,
        FailureRepository failureRepository, MinioConfig config) {
        this.videoRepository = videoRepository;
        this.failureRepository = failureRepository;
        this.config = config;
    }

    @Override
    public <T extends AmateurJaAdultEntry>
    void importLinkedRepository(String domain, int subtype, LinkedRepository<String, T> repository)
        throws OtherResponseException {
        Optional<IdView<String>> optional = videoRepository.getFirstOrderUpdateTime(domain);
        LinkedRepoIterator<String, T> iterator;
        if (optional.isPresent()) {
            iterator = repository.linkedRepoIterator(optional.get().getId());
            try {
                iterator.next();
            } catch (NotFoundException e) {
                throw new AppException(e);
            }
        } else {
            iterator = repository.linkedRepoIterator();
        }
        int success = 0, total = 0;
        Source source = Source.record(domain, subtype, 0);
        while (iterator.hasNext()) {
            T item = null;
            try {
                item = iterator.next();
            } catch (NotFoundException e) {
                throw new AppException(e);
            }
            success += insertAmateurEntry(item, source);
            total++;
        }
        log.info("Imported adult entries from {}: {} succeed, {} failed", domain, success,
            total - success);
    }

    @Override
    public <I, T extends IntIdentifier & AmateurJaAdultEntry & UpdateDatetimeSupplier, P extends PageReq>
    void importLatestByPage(String domain, int subtype,
        @Nonnull RepoPageable<P, PageResult<I, P>> pageable, P firstReq,
        RepoRetrievable<I, T> retrievable) throws OtherResponseException {
        LocalDateTime deadline = videoRepository.findLatestUpdate(domain, subtype).orElse(START);
        P req = firstReq;
        List<T> entities = new ArrayList<>();
        while (true) {
            PageResult<I, P> result = null;
            try {
                result = pageable.findPage(req);
            } catch (NotFoundException e) {
                throw new AppException(e);
            }
            boolean dead = false;
            for (I index : result.getContent()) {
                T t;
                try {
                    t = retrievable.findById(index);
                } catch (NotFoundException e) {
                    throw new AppException(e);
                }
                if (t.getUpdate().compareTo(deadline) <= 0) {
                    dead = true;
                    break;
                }
                entities.add(t);
            }
            if (dead || !result.hasNext()) {
                break;
            }
            req = result.nextPageRequest();
        }
        if (entities.isEmpty()) {
            return;
        }
        int success = 0, total = 0;
        for (int i = entities.size() - 1; i >= 0; i--) {
            T t = entities.get(i);
            Source source = Source.record(domain, subtype, t.getId());
            String serialNum = t.getSerialNum();
            if (serialNum != null) {
                success += insertAmateurEntry(t, source);
            }
            total++;
        }
        log.info("Imported adult entries from {}: {} succeed, {} failed", domain, success,
            total - success);
    }

    @Override
    public List<ImagePreview> findImages(Pageable pageable) {
        return videoRepository.findAllByImagesIsNotNullOrderByGmtModified();
    }

    /**
     * Saves an amateur entry.
     *
     * @return 0 if failed or 1 if succeeded
     */
    private int insertAmateurEntry(@Nonnull AmateurJaAdultEntry entry, Source source) {
        String serialNum = entry.getSerialNum();
        try {
            serialNum = SerialNumber.format(serialNum);
        } catch (IllegalArgumentException e) {
            String message = "The serial number is invalid: " + serialNum;
            failureRepository.insert(new Failure(source, message));
            return 0;
        }
        AdultVideoEntity entity = copyEntry(entry, source.getDomain(), source.getSubtype());
        entity.setSource(source);

        Optional<AdultVideoEntity> optional = videoRepository.findById(serialNum);
        if (optional.isEmpty()) {
            videoRepository.insert(entity);
            return 1;
        }

        AdultVideoEntity exists = optional.get();
        if (merge(exists, entity)) {
            String message = "The target serial number exists: " + serialNum;
            failureRepository.insert(new Failure(source, message));
            return 0;
        }
        if (exists.getImages() != null) {
            if (entity.getImages() == null) {
                entity.setImages(exists.getImages());
            } else {
                entity.getImages().addAll(exists.getImages());
            }
        }
        if (entity.getCover() == null) {
            entity.setCover(exists.getCover());
        } else if (exists.getCover() != null) {
            entity.getImages().add(exists.getCover());
        }
        Set<String> tags = new HashSet<>(videoRepository.findTagsById(serialNum));
        if (entity.getTags() != null) {
            tags.addAll(entity.getTags());
        }
        entity.setTags(new ArrayList<>(tags));
        videoRepository.updateById(entity);
        return 1;
    }

    private AdultVideoEntity copyEntry(AmateurJaAdultEntry entry, String domain, int subtype) {
        AdultVideoEntity entity = new AdultVideoEntity();
        String serialNum = entry.getSerialNum();
        entity.setId(serialNum);
        if (entry instanceof TitledAdultEntry) {
            entity.setTitle(((TitleSupplier) entry).getTitle());
        }
        try {
            entity.setCover(config.uploadCover(entry, domain, subtype, serialNum));
        } catch (NotFoundException | NotFiletypeException ignored) {
        } catch (OtherResponseException e) {
            throw new AppException(e);
        }
        entity.setMosaic(entry.getMosaic());
        entity.setDuration(entry.getDuration());
        entity.setReleaseDate(entry.getRelease());
        entity.setProducer(entry.getProducer());
        entity.setDistributor(entry.getDistributor());
        entity.setSeries(entry.getSeries());
        if (entry instanceof Tagged) {
            String[] tags = ((Tagged) entry).getTags();
            if (tags != null) {
                entity.setTags(Arrays.asList(tags));
            }
        }
        if (entry instanceof UpdateDatetimeSupplier) {
            entity.setUpdateTime(((UpdateDatetimeSupplier) entry).getUpdate());
        } else if (entry instanceof UpdateDateSupplier) {
            LocalDate date = ((UpdateDateSupplier) entry).getUpdate();
            entity.setUpdateTime(LocalDateTime.of(date, LocalTime.MIN));
        }
        if (entry instanceof AlbumSupplier) {
            try {
                AlbumSupplier supplier = (AlbumSupplier) entry;
                entity.setImages(config.uploadAlbum(supplier, domain, subtype, serialNum));
            } catch (NotFoundException | NotFiletypeException ignored) {
            } catch (OtherResponseException e) {
                throw new AppException(e);
            }
        }
        return entity;
    }

    private boolean merge(AdultVideoEntity old, AdultVideoEntity add) {
        return merge(old, add, AdultVideoEntity::getTitle, AdultVideoEntity::setTitle)
            || merge(old, add, AdultVideoEntity::getMosaic, AdultVideoEntity::setMosaic)
            || merge(old, add, AdultVideoEntity::getDuration, AdultVideoEntity::setDuration)
            || merge(old, add, AdultVideoEntity::getReleaseDate, AdultVideoEntity::setReleaseDate)
            || merge(old, add, AdultVideoEntity::getProducer, AdultVideoEntity::setProducer)
            || merge(old, add, AdultVideoEntity::getDistributor, AdultVideoEntity::setDistributor)
            || merge(old, add, AdultVideoEntity::getSeries, AdultVideoEntity::setSeries);
    }

    /**
     * Returns {@code true} if the two values of the specific property are both not null and not
     * equivalent. Otherwise, return {@code false} and merge the non-null value, if exists, to the
     * new entity.
     */
    private <T> boolean merge(AdultVideoEntity exists, AdultVideoEntity newEntity,
        @Nonnull Function<AdultVideoEntity, T> getter,
        @Nonnull BiConsumer<AdultVideoEntity, T> setter) {
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
}
