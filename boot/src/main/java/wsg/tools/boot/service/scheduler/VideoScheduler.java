package wsg.tools.boot.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wsg.tools.boot.pojo.result.ImportResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;

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

    private SubjectService subjectService;

    /**
     * Scheduler to update top 250 at 00:00 every Monday.
     */
    @Scheduled(cron = "0 0 0 ? * 1")
    @Transactional(rollbackFor = Exception.class)
    public void top250() {
        log.info("Start to update top 250.");
        logResult(subjectService.top250());
    }

    /**
     * Scheduler to update weekly movies at 03:00 every Saturday.
     */
    @Scheduled(cron = "0 0 3 ? * 6")
    public void movieWeekly() {
        log.info("Start to update weekly movies.");
        logResult(subjectService.movieWeekly());
    }

    /**
     * Scheduler to update us box movies at 06:00 every Saturday.
     */
    @Scheduled(cron = "0 0 6 ? * 6")
    public void movieUsBox() {
        log.info("Start to update us box movies.");
        logResult(subjectService.movieUsBox());
    }

    /**
     * Scheduler to update movies in theaters at 09:00 every day.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void movieInTheatre() {
        log.info("Start to update movies in theaters.");
        logResult(subjectService.movieInTheatre());
    }

    /**
     * Scheduler to update movies coming soon at 12:00 every day.
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void movieComingSoon() {
        log.info("Start to update movies coming soon.");
        logResult(subjectService.movieComingSoon());
    }

    /**
     * Scheduler to update new movies at 15:00 every day.
     */
    @Scheduled(cron = "0 0 15 * * *")
    public void newMovies() {
        log.info("Start to update new movies.");
        logResult(subjectService.newMovies());
    }

    private void logResult(ImportResult result) {
        if (result.isSuccess()) {
            log.info("Finished, added: {}, exists: {}, not found: {}, total: {}",
                    result.getAdded(), result.getExists(), result.getNotFounds(), result.getTotal());
        } else {
            log.error(result.getMessage());
        }
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
