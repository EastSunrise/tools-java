package wsg.tools.boot.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wsg.tools.boot.config.MinioConfig;
import wsg.tools.boot.dao.jpa.mapper.AdultVideoRepository;
import wsg.tools.boot.dao.jpa.mapper.FailureRepository;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity_;
import wsg.tools.boot.pojo.entity.base.Failure;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.RepoIterator;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.SiteUtils;
import wsg.tools.internet.info.adult.LicencePlateItem;
import wsg.tools.internet.info.adult.LicencePlateSite;
import wsg.tools.internet.info.adult.common.AdultEntry;
import wsg.tools.internet.info.adult.common.Mosaic;
import wsg.tools.internet.info.adult.common.SerialNumber;
import wsg.tools.internet.info.adult.midnight.BaseMidnightEntry;
import wsg.tools.internet.info.adult.midnight.MidnightAdultEntry;
import wsg.tools.internet.info.adult.midnight.MidnightAmateurEntryType;
import wsg.tools.internet.info.adult.midnight.MidnightColumn;
import wsg.tools.internet.info.adult.midnight.MidnightIndex;
import wsg.tools.internet.info.adult.midnight.MidnightPageRequest;
import wsg.tools.internet.info.adult.midnight.MidnightPageResult;
import wsg.tools.internet.info.adult.midnight.MidnightSite;

/**
 * @author Kingen
 * @see AdultService
 * @since 2021/3/5
 */
@Slf4j
@Service
public class AdultServiceImpl extends BaseServiceImpl implements AdultService {

    private static final String CODE_EXISTS_MSG = "The target code exists";

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
    public void importLicencePlateSite(@Nonnull LicencePlateSite site)
        throws OtherResponseException {
        String domain = site.getDomain();
        Sort sort = Sort.by(Sort.Direction.DESC, AdultVideoEntity_.GMT_CREATED);
        Pageable pageable = PageRequest.of(0, 1, sort);
        AdultVideoEntity probe = new AdultVideoEntity();
        probe.setSource(Source.repo(domain));
        Example<AdultVideoEntity> example = Example.of(probe);
        Page<AdultVideoEntity> page = videoRepository.findAll(example, pageable);

        LinkedRepository<String, LicencePlateItem> repository = site.getRepository();
        RepoIterator<LicencePlateItem> iterator;
        if (page.hasContent()) {
            String start = page.iterator().next().getId();
            iterator = repository.linkedRepoIterator(start);
            SiteUtils.found(iterator::next);
        } else {
            iterator = repository.linkedRepoIterator();
        }
        int success = 0, total = 0;
        while (iterator.hasNext()) {
            LicencePlateItem item = SiteUtils.found(iterator::next);
            AdultEntry entry = item.getEntry();
            Source source = Source.record(domain, Source.DEFAULT_SUBTYPE, 0);
            success += insertEntry(entry, source);
            total++;
        }
        log.info("Imported adult entries from {}: {} succeed, {} failed", domain, success,
            total - success);
    }

    @Override
    public void importMidnightEntries(@Nonnull MidnightSite site,
        @Nonnull MidnightAmateurEntryType type) throws OtherResponseException {
        MidnightColumn column = type.getColumn();
        Integer subtype = column.getCode();
        String domain = site.getDomain();
        long max = videoRepository.findMaxRid(domain, subtype).orElse(0L);

        MidnightPageRequest request = MidnightPageRequest.first();
        boolean finished = false;
        List<MidnightIndex> indices = new ArrayList<>();
        while (true) {
            MidnightPageResult page = SiteUtils.found(request, req -> site.findPage(column, req));
            for (MidnightIndex index : page.getContent()) {
                if (index.getId() <= max) {
                    finished = true;
                    break;
                }
                indices.add(index);
            }
            if (finished || !page.hasNext()) {
                break;
            }
            request = page.nextPageRequest();
        }

        indices.sort(Comparator.comparing(MidnightIndex::getId));
        int success = 0;
        for (MidnightIndex index : indices) {
            BaseMidnightEntry entry = null;
            try {
                entry = site.findAmateurEntry(type, index.getId());
            } catch (NotFoundException e) {
                throw new AppException(e);
            }
            if (entry instanceof MidnightAdultEntry) {
                Source source = Source.record(domain, subtype, entry.getId());
                success += insertEntry(((MidnightAdultEntry) entry).getEntry(), source);
            }
        }
        log.info("Imported adult entries of {} from {}: {} succeed, {} failed.", type, domain,
            success, indices.size() - success);
    }

    private int insertEntry(AdultEntry entry, Source source) {
        Objects.requireNonNull(entry, "the entry to save");
        Objects.requireNonNull(source, "the source of the entry");
        String code = entry.getCode();
        try {
            code = SerialNumber.format(entry.getCode());
        } catch (IllegalArgumentException e) {
            failureRepository.insert(new Failure(source, "The code is invalid: " + code));
            return 0;
        }
        if (videoRepository.findById(code).isPresent()) {
            failureRepository.insert(new Failure(source, CODE_EXISTS_MSG));
            return 0;
        }
        AdultVideoEntity entity = new AdultVideoEntity();
        entity.setId(code);
        entity.setTitle(entry.getTitle());
        Mosaic mosaic = entry.getMosaic();
        if (mosaic != null) {
            entity.setMosaic(mosaic.isCovered());
        }
        entity.setDuration(entry.getDuration());
        entity.setReleaseDate(entry.getRelease());
        entity.setDirector(entry.getDirector());
        entity.setProducer(entry.getProducer());
        entity.setDistributor(entry.getDistributor());
        entity.setSeries(entry.getSeries());
        entity.setTags(entry.getTags());
        entity.setSource(source);
        List<URL> images = entry.getImages();
        if (CollectionUtils.isNotEmpty(images)) {
            List<String> uploads = new ArrayList<>();
            for (URL image : images) {
                String upload = null;
                try {
                    upload = config.uploadEntryImage(image, code);
                } catch (NotFoundException ignored) {
                } catch (OtherResponseException e) {
                    throw new AppException(e);
                }
                uploads.add(upload);
            }
            entity.setImages(uploads);
        }
        videoRepository.insert(entity);
        return 1;
    }
}
