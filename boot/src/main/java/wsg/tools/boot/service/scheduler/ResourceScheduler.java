package wsg.tools.boot.service.scheduler;

import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wsg.tools.boot.config.SiteManager;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UnexpectedException;
import wsg.tools.internet.info.adult.LicencePlateSite;
import wsg.tools.internet.info.adult.midnight.MidnightAmateurColumn;
import wsg.tools.internet.info.adult.midnight.MidnightColumn;
import wsg.tools.internet.info.adult.midnight.MidnightPageReq;
import wsg.tools.internet.info.adult.midnight.MidnightSite;
import wsg.tools.internet.resource.movie.AbstractListResourceSite;
import wsg.tools.internet.resource.movie.BaseIdentifiedItem;

/**
 * Scheduled tasks.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Slf4j
@Service
public class ResourceScheduler extends BaseServiceImpl {

    private static final int DEFAULT_SUBTYPE = Source.DEFAULT_SUBTYPE;

    private final ResourceService resourceService;
    private final AdultService adultService;
    private final SiteManager manager;

    @Autowired
    public ResourceScheduler(ResourceService resourceService,
        AdultService adultService, SiteManager manager) {
        this.resourceService = resourceService;
        this.adultService = adultService;
        this.manager = manager;
    }

    @Scheduled(cron = "0 0 13 * * ?")
    public void importBdMovie() {
        importIntRange(manager.bdMovieSite());
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void importMovieHeaven() {
        importIntRange(manager.movieHeavenSite());
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void importXlc() {
        importIntRange(manager.xlcSite());
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void importXlm() {
        importIntRange(manager.xlmSite());
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void importY80s() {
        importIntRange(manager.y80sSite());
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void importGrapeNews() {
        importIntRange(manager.grapeSite());
    }

    @Scheduled(cron = "0 0 11 * * ?")
    public void importLicencePlate() {
        LicencePlateSite site = manager.licencePlateSite();
        String domain = site.getDomain();
        try {
            adultService.importLinkedRepository(domain, DEFAULT_SUBTYPE, site.getRepository());
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void importMidnight() {
        MidnightSite site = manager.midnightSite();
        String domain = site.getDomain();
        for (MidnightAmateurColumn type : MidnightAmateurColumn.values()) {
            MidnightColumn column = type.getColumn();
            int subtype = column.getCode();
            MidnightPageReq first = MidnightPageReq.first();
            try {
                adultService.importLatestByPage(domain, subtype, req -> site.findPage(column, req),
                    first, index -> site.findAmateurEntry(type, index.getId()));
            } catch (OtherResponseException e) {
                log.error(e.getMessage());
            }
        }
    }

    private <T extends BaseIdentifiedItem, S extends AbstractListResourceSite<T>>
    void importIntRange(@Nonnull S site) {
        try {
            ListRepository<Integer, T> repository = site.getRepository();
            resourceService.importIntListRepository(repository, site.getDomain());
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
        } catch (UnexpectedException e) {
            log.error(e.getCause().getMessage());
        }
    }
}
