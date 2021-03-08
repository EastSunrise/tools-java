package wsg.tools.boot.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.common.enums.SerialNumHeaderEnum;
import wsg.tools.boot.config.FastdfsClient;
import wsg.tools.boot.dao.jpa.mapper.AdultVideoRepository;
import wsg.tools.boot.dao.jpa.mapper.FailureRepository;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity_;
import wsg.tools.boot.pojo.entity.base.Failure;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.base.impl.LinkedRepositoryImpl;
import wsg.tools.internet.base.intf.RepositoryIterator;
import wsg.tools.internet.download.FileExistStrategy;
import wsg.tools.internet.download.impl.BasicDownloader;
import wsg.tools.internet.info.adult.AdultEntry;
import wsg.tools.internet.info.adult.LaymanCatItem;
import wsg.tools.internet.info.adult.LaymanCatSite;
import wsg.tools.internet.info.adult.common.Mosaic;
import wsg.tools.internet.info.adult.midnight.MidnightEntry;
import wsg.tools.internet.info.adult.midnight.MidnightEntryType;
import wsg.tools.internet.info.adult.midnight.MidnightPageRequest;
import wsg.tools.internet.info.adult.midnight.MidnightPageResult;
import wsg.tools.internet.info.adult.midnight.MidnightSimpleItem;
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
    private static final String COVER_NOT_EXIST_MSG = "The cover doesn't exist";

    private final FastdfsClient client;
    private final AdultVideoRepository videoRepository;
    private final FailureRepository failureRepository;

    private final LaymanCatSite laymanCatSite = new LaymanCatSite();
    private final MidnightSite midnightSite = new MidnightSite();
    private final BasicDownloader downloader = new BasicDownloader()
        .strategy(FileExistStrategy.REPLACE);

    @Autowired
    public AdultServiceImpl(FastdfsClient client, AdultVideoRepository videoRepository,
        FailureRepository failureRepository) {
        this.client = client;
        this.videoRepository = videoRepository;
        this.failureRepository = failureRepository;
    }

    @Override
    public void importLaymanCatSite() throws HttpResponseException {
        Sort sort = Sort.by(Sort.Direction.DESC, AdultVideoEntity_.GMT_CREATED);
        Pageable pageable = PageRequest.of(0, 1, sort);
        AdultVideoEntity probe = new AdultVideoEntity();
        probe.setSource(Source.repo(laymanCatSite.getDomain()));
        Example<AdultVideoEntity> example = Example.of(probe);
        Page<AdultVideoEntity> page = videoRepository.findAll(example, pageable);
        RepositoryIterator<LaymanCatItem> iterator;
        if (page.hasContent()) {
            long rid = page.iterator().next().getSource().getRid();
            String first = SerialNumHeaderEnum.deserialize(rid);
            iterator = new LinkedRepositoryImpl<>(laymanCatSite, first).iterator();
            iterator.next();
        } else {
            iterator = laymanCatSite.iterator();
        }
        int success = 0, total = 0;
        while (iterator.hasNext()) {
            LaymanCatItem item = iterator.next();
            AdultEntry entry = item.getEntry();
            long rid = SerialNumHeaderEnum.serialize(item.getId());
            Source source = Source.record(laymanCatSite.getDomain(), rid);
            success += insertEntry(entry, source, null);
            total++;
        }
        log.info("Imported adult entries from {}: {} succeed, {} failed", laymanCatSite.getDomain(),
            success, total - success);
    }

    @Override
    public void importMidnightEntries(MidnightEntryType type) throws HttpResponseException {
        Integer subtype = type.getType().getCode();
        String domain = midnightSite.getDomain();
        long max = videoRepository.findMaxRid(domain, subtype).orElse(0L);

        MidnightPageRequest request = MidnightPageRequest.first();
        boolean finished = false;
        List<MidnightSimpleItem> items = new ArrayList<>();
        while (true) {
            MidnightPageResult page = midnightSite.getSimpleItemPage(type.getType(), request);
            for (MidnightSimpleItem simpleItem : page.getContent()) {
                if (simpleItem.getId() <= max) {
                    finished = true;
                    break;
                }
                items.add(simpleItem);
            }
            if (finished || !page.hasNext()) {
                break;
            }
            request = page.nextPageRequest();
        }

        items.sort(Comparator.comparing(MidnightSimpleItem::getId));
        int success = 0;
        for (MidnightSimpleItem simpleItem : items) {
            MidnightEntry entry = midnightSite.findAdultEntry(type, simpleItem.getId());
            Source source = Source.record(domain, subtype, (long) entry.getId());
            success += insertEntry(entry.getEntry(), source, entry.getImages());
        }
        log.info("Imported adult entries of {} from {}: {} succeed, {} failed.", type, domain,
            success, items.size() - success);
    }

    private int insertEntry(AdultEntry entry, Source source, List<String> images) {
        if (entry == null) {
            failureRepository.insert(new Failure(source, CODE_NOT_EXIST_MSG));
            return 0;
        }
        if (videoRepository.findById(entry.getCode()).isPresent()) {
            failureRepository.insert(new Failure(source, CODE_EXISTS_MSG));
            return 0;
        }
        AdultVideoEntity entity = new AdultVideoEntity();
        entity.setId(entry.getCode());
        try {
            entity.setCover(upload(entry.getCover()));
        } catch (NotFoundException e) {
            failureRepository.insert(new Failure(source, COVER_NOT_EXIST_MSG));
            return 0;
        }
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
        if (CollectionUtils.isNotEmpty(images)) {
            List<String> list = new ArrayList<>();
            for (String image : images) {
                String upload = null;
                try {
                    upload = upload(image);
                } catch (NotFoundException ignored) {
                }
                list.add(upload);
            }
            entity.setImages(list);
        }
        videoRepository.insert(entity);
        return 1;
    }

    private String upload(String url) throws NotFoundException {
        try {
            File file = downloader.download(TMPDIR, new URL(url));
            return client.uploadLocal(file).getFullPath();
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
