package wsg.tools.boot.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wsg.tools.boot.dao.api.VideoConfig;
import wsg.tools.boot.dao.jpa.mapper.ChartRepository;
import wsg.tools.boot.dao.jpa.mapper.SubjectRepository;
import wsg.tools.boot.service.base.BaseServiceImpl;

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
    private ChartRepository chartRepository;

    /**
     * Scheduler to update top 250 at 00:00 every Monday.
     */
    @Scheduled(cron = "0 0 0 ? * 1")
    @Transactional(rollbackFor = Exception.class)
    public void top250() {
        log.info("Start to update top 250.");
    }

    /**
     * Scheduler to update weekly movies at 03:00 every Saturday.
     */
    @Scheduled(cron = "0 0 3 ? * 6")
    public void movieWeekly() throws HttpResponseException {
        log.info("Start to update weekly movies.");
    }

    /**
     * Scheduler to update movies in theaters at 06:00 every day.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void movieInTheatre() throws HttpResponseException {
        log.info("Start to update movies in theaters.");
    }

    /**
     * Scheduler to update new movies at 12:00 every day.
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void newMovies() throws HttpResponseException {
        log.info("Start to update new movies.");
    }

    @Autowired
    public void setConfig(VideoConfig config) {
        this.config = config;
    }

    @Autowired
    public void setSubjectRepository(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Autowired
    public void setChartRepository(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
    }
}
