package wsg.tools.boot.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import wsg.tools.boot.dao.jpa.mapper.JaAdultVideoRepository;
import wsg.tools.boot.dao.jpa.mapper.WesternAdultVideoRepository;
import wsg.tools.boot.pojo.entity.adult.JaAdultVideoEntity;
import wsg.tools.boot.pojo.entity.adult.WesternAdultVideoEntity;
import wsg.tools.boot.pojo.entity.base.Failure;
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
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.common.UpdateTemporalSupplier;
import wsg.tools.internet.info.adult.common.SerialNumber;
import wsg.tools.internet.info.adult.view.AlbumSupplier;
import wsg.tools.internet.info.adult.view.AmateurJaAdultEntry;
import wsg.tools.internet.info.adult.view.Classified;
import wsg.tools.internet.info.adult.view.JaAdultEntry;
import wsg.tools.internet.info.adult.view.PreviewSupplier;
import wsg.tools.internet.info.adult.view.Tagged;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;
import wsg.tools.internet.info.adult.west.WesternAdultEntry;
import wsg.tools.internet.info.adult.wiki.WikiAdultEntry;
import wsg.tools.internet.info.adult.wiki.WikiCelebrity;
import wsg.tools.internet.info.adult.wiki.WikiCelebrityIndex;

/**
 * @author Kingen
 * @see AdultService
 * @since 2021/3/5
 */
@Slf4j
@Service
public class AdultServiceImpl extends BaseServiceImpl implements AdultService {

    private static final LocalDateTime START = LocalDateTime.MIN;

    private final JaAdultVideoRepository jaVideoRepository;
    private final WesternAdultVideoRepository wtVideoRepository;
    private final FailureRepository failureRepository;
    private final MinioConfig config;
    private final TransactionTemplate template;

    @Autowired
    public AdultServiceImpl(JaAdultVideoRepository jaVideoRepository,
        WesternAdultVideoRepository wtVideoRepository,
        FailureRepository failureRepository, MinioConfig config,
        TransactionTemplate template) {
        this.jaVideoRepository = jaVideoRepository;
        this.wtVideoRepository = wtVideoRepository;
        this.failureRepository = failureRepository;
        this.config = config;
        this.template = template;
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
    public <I, T extends IntIdentifier & JaAdultEntry & UpdateDatetimeSupplier, P extends PageReq>
    void importLatestByPage(String domain, int subtype,
        @Nonnull RepoPageable<P, PageResult<I, P>> pageable, P firstReq,
        RepoRetrievable<I, T> retrievable) throws OtherResponseException {
        LocalDateTime deadline = jaVideoRepository.getLatestTimestamp(domain, subtype)
            .orElse(START);
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
            if (t.getSerialNum() != null) {
                Source source = Source.record(domain, subtype, t);
                success += saveJaAdultEntry(t, source);
            }
            total++;
        }
        log.info("Imported adult entries from {}: {} succeed, {} failed", domain, success,
            total - success);
    }

    @Override
    public void
    importCelebrityEntries(String domain, RepoRetrievable<String, WikiAdultEntry> retrievable,
        @Nonnull ListRepository<WikiCelebrityIndex, WikiCelebrity> repository)
        throws OtherResponseException {
        int start = jaVideoRepository.getMaxSubtype(domain).orElse(1);
        List<WikiCelebrityIndex> indices = repository.indices().stream()
            .filter(index -> index.getId() > start)
            .sorted(Comparator.comparing(WikiCelebrityIndex::getId))
            .collect(Collectors.toList());
        if (indices.isEmpty()) {
            return;
        }
        int success = 0, total = 0;
        for (WikiCelebrityIndex index : indices) {
            WikiCelebrity celebrity = null;
            try {
                celebrity = repository.findById(index);
            } catch (NotFoundException e) {
                continue;
            }
            Set<String> works = celebrity.getWorks();
            if (CollectionUtils.isEmpty(works)) {
                continue;
            }
            total += works.size();
            int celebrityId = celebrity.getId();
            success += Objects.requireNonNull(template.execute(status -> {
                int count = 0, id = 0;
                for (String work : works) {
                    WikiAdultEntry entry = null;
                    try {
                        entry = retrievable.findById(work);
                    } catch (NotFoundException e) {
                        continue;
                    } catch (OtherResponseException e) {
                        // rollback
                        throw new AppException(e);
                    }
                    Source source = Source.record(domain, celebrityId, id, null);
                    id++;
                    count += saveJaAdultEntry(entry, source);
                }
                return count;
            }));
        }
        log.info("Imported adult entries from {}: {} succeed, {} failed", domain, success,
            total - success);
    }

    @Override
    public int saveJaAdultEntry(@Nonnull JaAdultEntry entry, Source source) {
        String serialNum = entry.getSerialNum();
        try {
            serialNum = SerialNumber.format(serialNum);
        } catch (IllegalArgumentException e) {
            String message = "The serial number is invalid: " + serialNum;
            failureRepository.insert(new Failure(source, message));
            return 0;
        }
        JaAdultVideoEntity entity = copyEntry(entry, source.getDomain(), source.getSubtype());
        entity.setSource(source);

        Optional<JaAdultVideoEntity> optional = jaVideoRepository.findBySerialNum(serialNum);
        if (optional.isEmpty()) {
            jaVideoRepository.insert(entity);
            return 1;
        }

        JaAdultVideoEntity exists = optional.get();
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
        if (entity.getCoverURL() == null) {
            entity.setCover(exists.getCover());
        } else if (exists.getCoverURL() != null) {
            entity.getImages().add(exists.getCover());
        }
        Set<String> tags = new HashSet<>();
        if (exists.getTags() != null) {
            tags.addAll(Arrays.asList(exists.getTags()));
        }
        if (entity.getTags() != null) {
            tags.addAll(Arrays.asList(entity.getTags()));
        }
        entity.setTags(tags.toArray(new String[0]));
        entity.setId(exists.getId());
        jaVideoRepository.updateById(entity);
        return 1;
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
    public int
    saveWesternAdultEntry(@Nonnull WesternAdultEntry entry, Source source) {
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

    private JaAdultVideoEntity copyEntry(JaAdultEntry entry, String domain, int subtype) {
        JaAdultVideoEntity entity = new JaAdultVideoEntity();
        String serialNum = entry.getSerialNum();
        entity.setSerialNum(serialNum);
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
        entity.setRelease(entry.getRelease());
        entity.setProducer(entry.getProducer());
        entity.setDistributor(entry.getDistributor());
        entity.setSeries(entry.getSeries());
        if (entry instanceof Tagged) {
            entity.setTags(((Tagged) entry).getTags());
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

    private boolean merge(JaAdultVideoEntity old, JaAdultVideoEntity add) {
        return merge(old, add, JaAdultVideoEntity::getTitle, JaAdultVideoEntity::setTitle)
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
}
