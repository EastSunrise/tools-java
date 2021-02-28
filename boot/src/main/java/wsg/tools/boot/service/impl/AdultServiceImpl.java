package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsg.tools.boot.dao.jpa.mapper.AdultSeriesRepository;
import wsg.tools.boot.dao.jpa.mapper.AdultVideoRepository;
import wsg.tools.boot.pojo.entity.AdultSeriesEntity;
import wsg.tools.boot.pojo.entity.AdultVideoEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.internet.video.site.adult.MidnightAdultVideo;
import wsg.tools.internet.video.site.adult.MidnightItem;
import wsg.tools.internet.video.site.adult.MidnightSite;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Kingen
 * @since 2021/2/23
 */
@Slf4j
@Service
public class AdultServiceImpl extends BaseServiceImpl implements AdultService {

    private final AdultVideoRepository videoRepository;
    private final AdultSeriesRepository seriesRepository;

    @Autowired
    public AdultServiceImpl(AdultVideoRepository videoRepository, AdultSeriesRepository seriesRepository) {
        this.videoRepository = videoRepository;
        this.seriesRepository = seriesRepository;
    }

    @Override
    public void importLatest() throws HttpResponseException {
        LocalDateTime start = seriesRepository.findMaxAddTime().orElse(null);
        List<MidnightItem> items = MidnightSite.getInstance().findAllByRangeClosed(start, null);
        int itemsCount = 0, videoCount = 0;
        for (MidnightItem item : items) {
            if (seriesRepository.findById(item.getId()).isPresent()) {
                continue;
            }

            AdultSeriesEntity seriesEntity = new AdultSeriesEntity();
            seriesEntity.setId(item.getId());
            seriesEntity.setTitle(item.getTitle());
            seriesEntity.setKeyword(item.getKeyword());
            seriesEntity.setAddTime(item.getRelease());
            seriesRepository.insert(seriesEntity);
            itemsCount++;

            for (MidnightAdultVideo work : item.getWorks()) {
                AdultVideoEntity videoEntity = new AdultVideoEntity();
                videoEntity.setSid(item.getId());
                videoEntity.setImage(work.getCover());
                videoEntity.setCode(work.getCode());
                videoRepository.insert(videoEntity);
                videoCount++;
            }
        }
        log.info("Finished importing: {} items, {} videos.", itemsCount, videoCount);
    }
}
