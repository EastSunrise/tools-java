package wsg.tools.boot.service.scheduler;

import java.util.function.Function;
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
import wsg.tools.internet.info.adult.licence.LicencePlateSite;
import wsg.tools.internet.info.adult.midnight.MidnightAmateurColumn;
import wsg.tools.internet.info.adult.midnight.MidnightColumn;
import wsg.tools.internet.info.adult.midnight.MidnightPageReq;
import wsg.tools.internet.info.adult.midnight.MidnightSite;
import wsg.tools.internet.info.adult.west.PornTubeSite;
import wsg.tools.internet.info.adult.wiki.CelebrityWikiSite;
import wsg.tools.internet.movie.resource.AbstractListResourceSite;
import wsg.tools.internet.movie.resource.BdMovieType;
import wsg.tools.internet.movie.resource.GrapeVodType;
import wsg.tools.internet.movie.resource.MovieHeavenType;
import wsg.tools.internet.movie.resource.XlcType;
import wsg.tools.internet.movie.resource.XlmColumn;
import wsg.tools.internet.movie.resource.view.IdentifierItem;

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
        importIntRange(manager.bdMovieSite(), BdMovieType::ordinal);
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void importMovieHeaven() {
        importIntRange(manager.movieHeavenSite(), MovieHeavenType::getId);
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void importXlc() {
        importIntRange(manager.xlcSite(), XlcType::getId);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void importXlm() {
        importIntRange(manager.xlmSite(), XlmColumn::getCode);
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void importGrapeNews() {
        importIntRange(manager.grapeSite(), GrapeVodType::getId);
    }

    @Scheduled(cron = "0 0 11 * * ?")
    public void importLicencePlate() {
        LicencePlateSite site = manager.licencePlateSite();
        String hostname = site.getHostname();
        try {
            adultService.importLinkedRepository(hostname, DEFAULT_SUBTYPE, site.getRepository());
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void importMidnight() {
        MidnightSite site = manager.midnightSite();
        for (MidnightAmateurColumn type : MidnightAmateurColumn.values()) {
            MidnightColumn column = type.getColumn();
            MidnightPageReq first = MidnightPageReq.first(column);
            int subtype = column.getId();
            try {
                adultService.importLatestByPage(site.getHostname(), subtype, site::findPage, first,
                    index -> site.findAmateurEntry(type, index.getId()));
            } catch (OtherResponseException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void importMidnightFormally() {
        MidnightColumn column = MidnightColumn.ENTRY;
        MidnightSite site = manager.midnightSite();
        MidnightPageReq first = MidnightPageReq.first(column);
        int subtype = column.getId();
        try {
            adultService.importLatestByPage(site.getHostname(), subtype, site::findPage, first,
                index -> site.findFormalEntry(index.getId()));
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void importCelebrityWiki() {
        CelebrityWikiSite site = manager.celebrityWikiSite();
        String domain = site.getHostname();
        try {
            adultService.importCelebrityEntries(domain, site::findAdultEntry, site.getRepository());
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 6 * * ?")
    public void importPornTubeSite() {
        PornTubeSite site = manager.pornTubeSite();
        try {
            adultService.importIntListRepository(site.getHostname(), 0, site.getRepository());
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
        }
    }

    private <E extends Enum<E>, T extends IdentifierItem<E>, S extends AbstractListResourceSite<T>>
    void importIntRange(@Nonnull S site, Function<E, Integer> subtypeFunc) {
        try {
            ListRepository<Integer, T> repository = site.getRepository();
            resourceService.importIntListRepository(repository, site.getHostname(), subtypeFunc);
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
        }
    }
}
