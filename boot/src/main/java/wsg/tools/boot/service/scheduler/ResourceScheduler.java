package wsg.tools.boot.service.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.config.SiteManager;
import wsg.tools.boot.dao.api.impl.GggGoodAdapter;
import wsg.tools.boot.dao.jpa.mapper.JaAdultVideoRepository;
import wsg.tools.boot.pojo.entity.adult.JaAdultVideoEntity;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.SiteUtils;
import wsg.tools.internet.info.adult.ggg.GggCategory;
import wsg.tools.internet.info.adult.ggg.GggGood;
import wsg.tools.internet.info.adult.ggg.GggGoodView;
import wsg.tools.internet.info.adult.ggg.GggPageReq;
import wsg.tools.internet.info.adult.ggg.GggSite;
import wsg.tools.internet.info.adult.licence.LicencePlateSite;
import wsg.tools.internet.info.adult.midnight.MidnightAmateurColumn;
import wsg.tools.internet.info.adult.midnight.MidnightColumn;
import wsg.tools.internet.info.adult.midnight.MidnightIndex;
import wsg.tools.internet.info.adult.midnight.MidnightPageReq;
import wsg.tools.internet.info.adult.midnight.MidnightSite;
import wsg.tools.internet.info.adult.view.JaAdultEntry;
import wsg.tools.internet.info.adult.west.PornTubeSite;
import wsg.tools.internet.info.adult.wiki.CelebrityWikiSite;
import wsg.tools.internet.info.adult.wiki.WikiAdultEntry;
import wsg.tools.internet.info.adult.wiki.WikiCelebrity;
import wsg.tools.internet.info.adult.wiki.WikiCelebrityIndex;
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
    private static final LocalDate MIDNIGHT_START = LocalDate.of(2019, 1, 1);
    private static final LocalDate GGG_START = LocalDate.of(2010, 1, 1);

    private final ResourceService resourceService;
    private final AdultService adultService;
    private final SiteManager manager;
    private final JaAdultVideoRepository jaVideoRepository;
    private final TransactionTemplate template;

    @Autowired
    public ResourceScheduler(ResourceService resourceService,
        AdultService adultService, SiteManager manager,
        JaAdultVideoRepository jaVideoRepository,
        TransactionTemplate template) {
        this.resourceService = resourceService;
        this.adultService = adultService;
        this.manager = manager;
        this.jaVideoRepository = jaVideoRepository;
        this.template = template;
    }

    @Scheduled(cron = "0 0 13 * * ?")
    public void importBdMovie() {
        importIntRange(manager.bdMovieSite(), BdMovieType::ordinal);
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void importMovieHeaven() {
        importIntRange(manager.movieHeavenSite(), MovieHeavenType::getId);
    }

    @Scheduled(cron = "0 30 5 * * ?")
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
    public void importMidnightAmateur() {
        MidnightSite site = manager.midnightSite();
        String domain = site.getHostname();
        for (MidnightAmateurColumn type : MidnightAmateurColumn.values()) {
            MidnightColumn column = type.getColumn();
            importMidnight(domain, column, site, id -> site.findAmateurEntry(type, id));
        }
    }

    @Scheduled(cron = "0 30 18 * * ?")
    public void importMidnightFormally() {
        MidnightColumn column = MidnightColumn.ENTRY;
        MidnightSite site = manager.midnightSite();
        importMidnight(site.getHostname(), column, site, site::findFormalEntry);
    }

    private <T extends IntIdentifier & JaAdultEntry> void importMidnight(String domain,
        MidnightColumn column, MidnightSite site, RepoRetrievable<Integer, T> retrievable) {
        int subtype = column.getId();
        LocalDate start = jaVideoRepository.getLatestTimestamp(domain, subtype)
            .map(LocalDateTime::toLocalDate).orElse(MIDNIGHT_START).plusDays(1);
        LocalDate end = LocalDate.now().minusDays(1);
        MidnightPageReq first = new MidnightPageReq(0, MidnightPageReq.MAX_PAGE_SIZE, column,
            start, end, MidnightPageReq.OrderBy.UPDATE);
        try {
            List<MidnightIndex> indices = SiteUtils.collectPage(site, first);
            if (indices.isEmpty()) {
                return;
            }
            indices.sort(Comparator.comparing(MidnightIndex::getUpdate));
            BatchResult<Integer> result = BatchResult.empty();
            for (MidnightIndex index : indices) {
                int id = index.getId();
                T entry = retrievable.findById(id);
                Source source = Source.record(domain, subtype, entry);
                adultService.saveJaAdultEntry(entry, source)
                    .ifPresentOrElse(reason -> result.fail(id, reason.getText()), result::succeed);
            }
            log.info("Imported {} entries from {}:", column, domain);
            result.print(String::valueOf);
        } catch (NotFoundException | OtherResponseException e) {
            log.error(e.getMessage());
            throw new AppException(e);
        }
    }

    public void importGgg() {
        GggSite site = manager.gggSite();
        String domain = site.getHostname();
        for (GggCategory category : GggCategory.all()) {
            int subtype = category.getCode();
            Optional<LocalDateTime> op = jaVideoRepository.getLatestTimestamp(domain, subtype);
            Set<Source> exists = new HashSet<>();
            op.map(dt -> findVideosByTimestamp(domain, dt)).map(List::stream)
                .ifPresent(st -> st.map(JaAdultVideoEntity::getSource).forEach(exists::add));
            LocalDate start = op.map(LocalDateTime::toLocalDate).orElse(GGG_START);
            try {
                List<GggGood> goods = SiteUtils.collectPageUntil(site, GggPageReq.byDate(category),
                    good -> good.getUpdate().isBefore(start));
                goods.sort(Comparator.comparing(GggGood::getUpdate));
                BatchResult<Integer> result = BatchResult.empty();
                Iterator<GggGood> iterator = goods.iterator();
                // goods updated at the start date
                while (iterator.hasNext()) {
                    GggGood good = iterator.next();
                    Source source = Source.record(domain, subtype, good);
                    if (exists.contains(source)) {
                        continue;
                    }
                    int id = good.getId();
                    GggGoodView view = site.findById(id);
                    GggGoodAdapter adapter = new GggGoodAdapter(good, view);
                    adultService.saveJaAdultEntry(adapter, source)
                        .ifPresentOrElse(r -> result.fail(id, r.getText()), result::succeed);
                    if (good.getUpdate().isAfter(start)) {
                        break;
                    }
                }
                // goods updated after the start date
                while (iterator.hasNext()) {
                    GggGood good = iterator.next();
                    int id = good.getId();
                    Source source = Source.record(domain, subtype, good);
                    GggGoodView view = site.findById(id);
                    GggGoodAdapter adapter = new GggGoodAdapter(good, view);
                    adultService.saveJaAdultEntry(adapter, source)
                        .ifPresentOrElse(r -> result.fail(id, r.getText()), result::succeed);
                }
                log.info("Imported {} entries from {}:", category, domain);
                result.print(String::valueOf);
            } catch (NotFoundException | OtherResponseException e) {
                log.error(e.getMessage());
                throw new AppException(e);
            }
        }
    }

    private List<JaAdultVideoEntity> findVideosByTimestamp(String domain, LocalDateTime timestamp) {
        Source source = new Source();
        source.setDomain(domain);
        source.setTimestamp(timestamp);
        JaAdultVideoEntity probe = new JaAdultVideoEntity();
        probe.setSource(source);
        return jaVideoRepository.findAll(Example.of(probe));
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void importCelebrityWiki() {
        CelebrityWikiSite site = manager.celebrityWikiSite();
        String domain = site.getHostname();
        int start = jaVideoRepository.getMaxSubtype(domain).orElse(1);
        ListRepository<WikiCelebrityIndex, WikiCelebrity> repository = null;
        try {
            repository = site.getRepository();
        } catch (OtherResponseException e) {
            throw new AppException(e);
        }
        List<WikiCelebrityIndex> indices = repository.indices().stream()
            .filter(index -> index.getId() > start)
            .sorted(Comparator.comparing(WikiCelebrityIndex::getId))
            .collect(Collectors.toList());
        if (indices.isEmpty()) {
            return;
        }
        BatchResult<String> result = BatchResult.empty();
        for (WikiCelebrityIndex index : indices) {
            WikiCelebrity celebrity = null;
            try {
                celebrity = site.findById(index);
            } catch (NotFoundException e) {
                continue;
            } catch (OtherResponseException e) {
                throw new AppException(e);
            }
            Set<String> works = celebrity.getWorks();
            if (CollectionUtils.isEmpty(works)) {
                continue;
            }
            int celebrityId = celebrity.getId();
            template.execute(status -> {
                int id = 0;
                for (String work : works) {
                    WikiAdultEntry entry = null;
                    try {
                        entry = site.findAdultEntry(work);
                    } catch (NotFoundException e) {
                        result.fail(work, "Not Found");
                        continue;
                    } catch (OtherResponseException e) {
                        // rollback
                        throw new AppException(e);
                    }
                    Source source = Source.record(domain, celebrityId, id, null);
                    adultService.saveJaAdultEntry(entry, source)
                        .ifPresentOrElse(r -> result.fail(work, r.getText()), result::succeed);
                    id++;
                }
                return null;
            });
        }
        log.info("Imported adult entries from {}:", domain);
        result.print(Function.identity());
    }

    @Scheduled(cron = "0 0 8 * * ?")
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
