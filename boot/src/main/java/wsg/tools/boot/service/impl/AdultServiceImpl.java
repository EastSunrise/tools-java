package wsg.tools.boot.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.common.util.OtherHttpResponseException;
import wsg.tools.boot.common.util.SiteUtilExt;
import wsg.tools.boot.config.FastdfsClient;
import wsg.tools.boot.dao.jpa.mapper.AdultVideoRepository;
import wsg.tools.boot.dao.jpa.mapper.FailureRepository;
import wsg.tools.boot.pojo.entity.EntityUtils;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity_;
import wsg.tools.boot.pojo.entity.base.Failure;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.base.intf.RepositoryIterator;
import wsg.tools.internet.download.FileExistStrategy;
import wsg.tools.internet.download.impl.BasicDownloader;
import wsg.tools.internet.info.adult.LaymanCatItem;
import wsg.tools.internet.info.adult.LaymanCatSite;
import wsg.tools.internet.info.adult.common.AdultEntry;
import wsg.tools.internet.info.adult.common.Mosaic;
import wsg.tools.internet.info.adult.midnight.BaseMidnightEntry;
import wsg.tools.internet.info.adult.midnight.MidnightAdultEntry;
import wsg.tools.internet.info.adult.midnight.MidnightIndex;
import wsg.tools.internet.info.adult.midnight.MidnightLaymanEntryType;
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

    private static final File TMPDIR = new File(Constants.SYSTEM_TMPDIR);
    private static final String CODE_EXISTS_MSG = "The target code exists";
    private static final String CODE_NOT_EXIST_MSG = "The code doesn't exist";
    private static final int MAX_CODE_LENGTH = 15;
    private static final String ILLEGAL_CODE_MSG = "The code is illegal";

    private final FastdfsClient client;
    private final AdultVideoRepository videoRepository;
    private final FailureRepository failureRepository;

    private final BasicDownloader downloader = new BasicDownloader()
        .strategy(FileExistStrategy.FINISH);

    @Autowired
    public AdultServiceImpl(FastdfsClient client, AdultVideoRepository videoRepository,
        FailureRepository failureRepository) {
        this.client = client;
        this.videoRepository = videoRepository;
        this.failureRepository = failureRepository;
    }

    @Override
    public void importLaymanCatSite(@Nonnull LaymanCatSite site) throws OtherHttpResponseException {
        String domain = site.getDomain();
        Sort sort = Sort.by(Sort.Direction.DESC, AdultVideoEntity_.GMT_CREATED);
        Pageable pageable = PageRequest.of(0, 1, sort);
        AdultVideoEntity probe = new AdultVideoEntity();
        probe.setSource(Source.repo(domain));
        Example<AdultVideoEntity> example = Example.of(probe);
        Page<AdultVideoEntity> page = videoRepository.findAll(example, pageable);

        LinkedRepository<String, LaymanCatItem> repository = site.getRepository();
        RepositoryIterator<LaymanCatItem> iterator;
        if (page.hasContent()) {
            long rid = page.iterator().next().getSource().getRid();
            String start = EntityUtils.deserialize(rid);
            iterator = repository.iteratorAfter(start);
            SiteUtilExt.found(iterator::next);
        } else {
            iterator = repository.iterator();
        }
        int success = 0, total = 0;
        while (iterator.hasNext()) {
            LaymanCatItem item = SiteUtilExt.found(iterator::next);
            AdultEntry entry = item.getEntry();
            long rid = EntityUtils.serialize(item.getId());
            Source source = Source.record(domain, rid);
            success += insertEntry(entry, source);
            total++;
        }
        log.info("Imported adult entries from {}: {} succeed, {} failed", domain, success,
            total - success);
    }

    @Override
    public void importMidnightEntries(@Nonnull MidnightSite site,
        @Nonnull MidnightLaymanEntryType type)
        throws OtherHttpResponseException {
        Integer subtype = type.getColumn().getCode();
        String domain = site.getDomain();
        long max = videoRepository.findMaxRid(domain, subtype).orElse(0L);

        MidnightPageRequest request = MidnightPageRequest.first();
        boolean finished = false;
        List<MidnightIndex> indexes = new ArrayList<>();
        while (true) {
            MidnightPageResult page = SiteUtilExt
                .found(type.getColumn(), request, site::findAllIndexes);
            for (MidnightIndex index : page.getContent()) {
                if (index.getId() <= max) {
                    finished = true;
                    break;
                }
                indexes.add(index);
            }
            if (finished || !page.hasNext()) {
                break;
            }
            request = page.nextPageRequest();
        }

        indexes.sort(Comparator.comparing(MidnightIndex::getId));
        int success = 0;
        for (MidnightIndex index : indexes) {
            BaseMidnightEntry entry = SiteUtilExt.found(type, index.getId(), site::findLaymanEntry);
            if (entry instanceof MidnightAdultEntry) {
                Source source = Source.record(domain, subtype, entry.getId());
                success += insertEntry(((MidnightAdultEntry) entry).getEntry(), source);
            }
        }
        log.info("Imported adult entries of {} from {}: {} succeed, {} failed.", type, domain,
            success, indexes.size() - success);
    }

    private int insertEntry(AdultEntry entry, Source source) {
        if (entry == null) {
            failureRepository.insert(new Failure(source, CODE_NOT_EXIST_MSG));
            return 0;
        }
        if (entry.getCode().length() > MAX_CODE_LENGTH) {
            failureRepository.insert(new Failure(source, ILLEGAL_CODE_MSG));
            return 0;
        }
        if (videoRepository.findById(entry.getCode()).isPresent()) {
            failureRepository.insert(new Failure(source, CODE_EXISTS_MSG));
            return 0;
        }
        AdultVideoEntity entity = new AdultVideoEntity();
        entity.setId(entry.getCode());
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
        List<String> images = entry.getImages();
        if (CollectionUtils.isNotEmpty(images)) {
            List<String> uploads = new ArrayList<>();
            for (String image : images) {
                String upload = null;
                try {
                    upload = upload(image);
                } catch (NotFoundException ignored) {
                }
                uploads.add(upload);
            }
            entity.setImages(uploads);
        }
        videoRepository.insert(entity);
        return 1;
    }

    private String upload(String url) throws NotFoundException {
        try {
            File file = downloader.download(TMPDIR, new URL(url));
            NameValuePair meta = new NameValuePair(FastdfsClient.META_SOURCE, url);
            return client.uploadLocal(file, meta).getFullPath();
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException(e.getMessage());
            }
            throw new AppException(e);
        } catch (IOException | MyException e) {
            throw new AppException(e);
        }
    }
}
