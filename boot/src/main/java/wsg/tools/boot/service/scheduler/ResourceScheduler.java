package wsg.tools.boot.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Functions;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.util.OtherHttpResponseException;
import wsg.tools.boot.common.util.SiteUtilExt;
import wsg.tools.boot.config.SiteManager;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.intf.IntIdentifiedRepository;
import wsg.tools.internet.common.UnexpectedException;
import wsg.tools.internet.info.adult.midnight.MidnightLaymanEntryType;
import wsg.tools.internet.resource.movie.BdMovieSite;
import wsg.tools.internet.resource.movie.GrapeSite;
import wsg.tools.internet.resource.movie.IdentifiedItem;
import wsg.tools.internet.resource.movie.MovieHeavenSite;
import wsg.tools.internet.resource.movie.XlcSite;
import wsg.tools.internet.resource.movie.XlmSite;
import wsg.tools.internet.resource.movie.Y80sSite;

/**
 * Scheduled tasks.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Slf4j
@Service
public class ResourceScheduler extends BaseServiceImpl {

    private static final long MILLISECONDS_PER_HOUR = 3_600_000;
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

    @Scheduled(initialDelay = MILLISECONDS_PER_HOUR, fixedDelay = MILLISECONDS_PER_HOUR)
    public void importBdMovie() {
        importIntRange(manager.bdMovieSite(), BdMovieSite::getRepository);
    }

    @Scheduled(initialDelay = MILLISECONDS_PER_HOUR, fixedDelay = MILLISECONDS_PER_HOUR)
    public void importMovieHeaven() {
        importIntRange(manager.movieHeavenSite(), MovieHeavenSite::getRepository);
    }

    @Scheduled(initialDelay = MILLISECONDS_PER_HOUR, fixedDelay = MILLISECONDS_PER_HOUR)
    public void importXlc() {
        importIntRange(manager.xlcSite(), XlcSite::getRepository);
    }

    @Scheduled(initialDelay = MILLISECONDS_PER_HOUR, fixedDelay = MILLISECONDS_PER_HOUR)
    public void importXlm() {
        importIntRange(manager.xlmSite(), XlmSite::getRepository);
    }

    @Scheduled(initialDelay = MILLISECONDS_PER_HOUR, fixedDelay = MILLISECONDS_PER_HOUR)
    public void importY80s() {
        importIntRange(manager.y80sSite(), Y80sSite::getRepository);
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void importGrapeNews() {
        importIntRange(manager.grapeSite(), GrapeSite::getNewsRepository);
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void importAdultEntries() {
        try {
            adultService.importLaymanCatSite(manager.laymanCatSite());
        } catch (OtherHttpResponseException e) {
            log.error(e.getMessage());
        }
        for (MidnightLaymanEntryType type : MidnightLaymanEntryType.values()) {
            try {
                adultService.importMidnightEntries(manager.midnightSite(), type);
            } catch (OtherHttpResponseException e) {
                log.error(e.getMessage());
            }
        }
    }

    private <E extends Enum<E> & IntCodeSupplier, T extends IdentifiedItem<E>, S extends BaseSite>
    void importIntRange(S site,
        Functions.FailableFunction<S, IntIdentifiedRepository<T>, HttpResponseException> getRepo) {
        try {
            IntIdentifiedRepository<T> repository = SiteUtilExt.found(site, getRepo);
            resourceService.importIntRangeRepository(repository, site.getDomain());
        } catch (OtherHttpResponseException e) {
            log.error(e.getMessage());
        } catch (UnexpectedException e) {
            log.error(e.getCause().getMessage());
        }
    }
}
