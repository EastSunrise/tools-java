package wsg.tools.boot.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wsg.tools.boot.dao.api.VideoConfig;
import wsg.tools.boot.dao.jpa.mapper.SubjectRepository;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.internet.video.enums.CityEnum;
import wsg.tools.internet.video.site.DoubanSite;

import java.util.List;

/**
 * Schedulers to update movies.
 *
 * @author Kingen
 * @since 2020/7/25
 */
@Slf4j
@Component
@EnableScheduling
public class VideoScheduler extends BaseServiceImpl<SubjectDto, SubjectEntity, Long> {

    private VideoConfig videoConfig;
    private SubjectRepository repository;

    /**
     * Scheduler to update top 250 at 00:00 every Monday.
     */
    @Scheduled(cron = "0 0 0 ? * 1 *")
    public void top250() throws HttpResponseException {
        log.info("Start to update top 250.");
        List<SubjectEntity> subjects = videoConfig.subjects(DoubanSite::apiMovieTop250);
        int inserted = batchInsertIgnoreExists(subjects);
        log.info("Finish to update top 250, inserted: {}, ignore: {}", inserted, subjects.size() - inserted);
    }

    /**
     * Scheduler to update weekly movies at 00:00 every Saturday.
     */
    @Scheduled(cron = "0 0 0 ? * 6 *")
    public void movieWeekly() throws HttpResponseException {
        log.info("Start to update weekly movies.");
        List<SubjectEntity> subjects = videoConfig.subjects(DoubanSite::apiMovieWeekly);
        int inserted = batchInsertIgnoreExists(subjects);
        log.info("Finish to update weekly movies, inserted: {}, ignore: {}", inserted, subjects.size() - inserted);
    }

    /**
     * Scheduler to update movies in theaters at 06:00 every day.
     */
    @Scheduled(cron = "0 0 6 * * * *")
    public void movieInTheatre() throws HttpResponseException {
        log.info("Start to update movies in theaters.");
        List<SubjectEntity> subjects = videoConfig.subjects(site -> site.apiMovieInTheaters(CityEnum.BEIJING));
        int inserted = batchInsertIgnoreExists(subjects);
        log.info("Finish to update movies in theaters, inserted: {}, ignore: {}", inserted, subjects.size() - inserted);
    }

    /**
     * Scheduler to update new movies at 12:00 every day.
     */
    @Scheduled(cron = "0 0 12 * * * *")
    public void newMovies() throws HttpResponseException {
        log.info("Start to update new movies.");
        List<SubjectEntity> subjects = videoConfig.subjects(DoubanSite::apiMovieNewMovies);
        int inserted = batchInsertIgnoreExists(subjects);
        log.info("Finish to update new movies, inserted: {}, ignore: {}", inserted, subjects.size() - inserted);
    }

    private int batchInsertIgnoreExists(List<SubjectEntity> entities) {
        final int[] count = {0};
        entities.forEach(entity -> {
            if (repository.findByDbId(entity.getDbId()).isEmpty()) {
                repository.insert(entity);
                count[0]++;
            }
        });
        return count[0];
    }

    @Autowired
    public void setVideoConfig(VideoConfig videoConfig) {
        this.videoConfig = videoConfig;
    }

    @Autowired
    public void setRepository(SubjectRepository repository) {
        this.repository = repository;
    }
}
