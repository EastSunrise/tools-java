package wsg.tools.boot.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wsg.tools.boot.dao.jpa.mapper.AdultVideoRepository;
import wsg.tools.boot.dao.jpa.mapper.FailureRepository;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity_;
import wsg.tools.boot.pojo.entity.base.Failure;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.base.impl.IterableRepositoryImpl;
import wsg.tools.internet.base.intf.RepositoryIterator;
import wsg.tools.internet.info.adult.AdultEntry;
import wsg.tools.internet.info.adult.LaymanCatItem;
import wsg.tools.internet.info.adult.LaymanCatSite;
import wsg.tools.internet.info.adult.common.Mosaic;

/**
 * @author Kingen
 * @see AdultService
 * @since 2021/3/5
 */
@Slf4j
@Service
public class AdultServiceImpl extends BaseServiceImpl implements AdultService {

    private final AdultVideoRepository videoRepository;
    private final FailureRepository failureRepository;
    private final LaymanCatSite laymanCatSite = new LaymanCatSite();

    @Autowired
    public AdultServiceImpl(AdultVideoRepository videoRepository,
        FailureRepository failureRepository) {
        this.videoRepository = videoRepository;
        this.failureRepository = failureRepository;
    }

    @Override
    public BatchResult<String> importLaymanCatSite() throws HttpResponseException {
        Sort sort = Sort.by(Sort.Direction.DESC, AdultVideoEntity_.GMT_CREATED);
        Pageable pageable = PageRequest.of(0, 1, sort);
        Iterator<AdultVideoEntity> page = videoRepository.findAll(pageable).iterator();
        RepositoryIterator<LaymanCatItem> iterator = laymanCatSite.iterator();
        if (page.hasNext()) {
            String id = page.next().getId();
            iterator = new IterableRepositoryImpl<>(laymanCatSite, id).iterator();
            iterator.next();
        }
        int count = 0;
        List<Failure> failures = new ArrayList<>();
        Map<String, String> fails = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        while (iterator.hasNext()) {
            LaymanCatItem item = iterator.next();
            AdultEntry entry = item.getEntry();
            String code = entry.getCode();
            Source source = new Source(laymanCatSite.getDomain(), item.getId());
            if (videoRepository.findById(code).isPresent()) {
                failures.add(new Failure(source, "The target code exists"));
                continue;
            }
            AdultVideoEntity entity = new AdultVideoEntity();
            entity.setId(code);
            entity.setCover(entry.getCover());
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
            videoRepository.insert(entity);
            count++;
        }
        for (Failure failure : failures) {
            failureRepository.insert(failure);
        }
        log.info("Imported repository of {}: {} succeed, {} failed", laymanCatSite.getDomain(),
            count, failures.size());
        return new BatchResult<>(count, fails);
    }
}
