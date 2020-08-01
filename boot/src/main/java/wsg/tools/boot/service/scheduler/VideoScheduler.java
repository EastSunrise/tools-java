package wsg.tools.boot.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wsg.tools.boot.dao.api.VideoConfig;
import wsg.tools.boot.dao.jpa.mapper.SubjectRepository;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.internet.video.entity.douban.container.RankedResult;
import wsg.tools.internet.video.entity.douban.pojo.SimpleSubject;
import wsg.tools.internet.video.enums.CityEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Schedulers to update movies.
 *
 * @author Kingen
 * @since 2020/7/25
 */
@Slf4j
@Component
@EnableScheduling
public class VideoScheduler extends BaseServiceImpl {

    private VideoConfig config;
    private SubjectRepository subjectRepository;

    /**
     * Scheduler to update top 250 at 00:00 every Monday.
     */
    @Scheduled(cron = "0 0 0 ? * 1")
    @Transactional(rollbackFor = Exception.class)
    public void top250() throws HttpResponseException {
        log.info("Start to update top 250.");
        batchInsertDoubanIgnore(config.doubanSite().apiMovieTop250().getRight());
        log.info("Finish to update top 250.");
    }

    /**
     * Scheduler to update weekly movies at 03:00 every Saturday.
     */
    @Scheduled(cron = "0 0 3 ? * 6")
    public void movieWeekly() throws HttpResponseException {
        log.info("Start to update weekly movies.");
        batchInsertDoubanIgnore(config.doubanSite().apiMovieWeekly().getSubjects().stream()
                .map(RankedResult.RankedSubject::getSubject).collect(Collectors.toList()));
        log.info("Finish to update weekly movies.");
    }

    /**
     * Scheduler to update movies in theaters at 06:00 every day.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void movieInTheatre() throws HttpResponseException {
        log.info("Start to update movies in theaters.");
        batchInsertDoubanIgnore(config.doubanSite().apiMovieInTheaters(CityEnum.BEIJING).getRight());
        log.info("Finish to update movies in theaters.");
    }

    /**
     * Scheduler to update new movies at 12:00 every day.
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void newMovies() throws HttpResponseException {
        log.info("Start to update new movies.");
        batchInsertDoubanIgnore(config.doubanSite().apiMovieNewMovies().getRight());
        log.info("Finish to update new movies.");
    }

    private void batchInsertDoubanIgnore(List<SimpleSubject> subjects) {
        int added = 0, exists = 0, notFound = 0;
        for (SimpleSubject subject : subjects) {
            if (subjectRepository.findByDbId(subject.getId()).isEmpty()) {
                SubjectEntity entity = config.getSubjectEntity(subject.getId(), null);
                if (entity != null) {
                    subjectRepository.insert(entity);
                    added++;
                    continue;
                }
                notFound++;
                continue;
            }
            exists++;
        }
        log.info("Finished, added: {}, exists: {}, not found: {}, total: {}", added, exists, notFound, subjects.size());
    }

    @Autowired
    public void setConfig(VideoConfig config) {
        this.config = config;
    }

    @Autowired
    public void setSubjectRepository(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
}
