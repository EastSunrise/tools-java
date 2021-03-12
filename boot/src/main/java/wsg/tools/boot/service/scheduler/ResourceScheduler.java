package wsg.tools.boot.service.scheduler;

import lombok.extern.slf4j.Slf4j;
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
import wsg.tools.common.util.function.throwable.ThrowableFunction;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.intf.IntRangeIdentifiedRepository;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.info.adult.midnight.MidnightLaymanEntryType;
import wsg.tools.internet.resource.movie.BdMovieItem;
import wsg.tools.internet.resource.movie.BdMovieType;
import wsg.tools.internet.resource.movie.GrapeSite;
import wsg.tools.internet.resource.movie.IdentifiedItem;
import wsg.tools.internet.resource.movie.MovieHeavenSite;
import wsg.tools.internet.resource.movie.XlcSite;
import wsg.tools.internet.resource.movie.XlmColumn;
import wsg.tools.internet.resource.movie.XlmItem;
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

    @Scheduled(cron = "0 0 9 * * ?")
    public void importLatestResources() {
        String bdDomain = manager.bdMovieSite().getDomain();
        for (BdMovieType type : BdMovieType.values()) {
            try {
                LinkedRepository<Integer, BdMovieItem> repository = manager.bdMovieSite()
                    .getRepository(type);
                resourceService.importLinkedRepository(repository, bdDomain, type.ordinal());
            } catch (OtherHttpResponseException e) {
                log.error(e.getMessage());
            }
        }
        importIntRange(manager.xlcSite(), XlcSite::getRepository);
        String xlmDomain = manager.xlmSite().getDomain();
        for (XlmColumn type : XlmColumn.values()) {
            try {
                LinkedRepository<Integer, XlmItem> repository = manager.xlmSite()
                    .getRepository(type);
                resourceService.importLinkedRepository(repository, xlmDomain, type.getCode());
            } catch (OtherHttpResponseException e) {
                log.error(e.getMessage());
            }
        }
        importIntRange(manager.y80sSite(), Y80sSite::getRepository);
        importIntRange(manager.movieHeavenSite(), MovieHeavenSite::getRepository);
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void importFixedResources() {
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
        ThrowableFunction<S, IntRangeIdentifiedRepository<T>, HttpResponseException> getRepo) {
        try {
            IntRangeIdentifiedRepository<T> repository = SiteUtilExt.found(site, getRepo);
            resourceService.importIntRangeRepository(repository, site.getDomain());
        } catch (OtherHttpResponseException e) {
            log.error(e.getMessage());
        }
    }
}
