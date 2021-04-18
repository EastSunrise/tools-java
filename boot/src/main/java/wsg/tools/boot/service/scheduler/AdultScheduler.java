package wsg.tools.boot.service.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.config.SiteManager;
import wsg.tools.boot.dao.api.adapter.impl.CelebrityAdapter;
import wsg.tools.boot.dao.api.impl.GggGoodAdapter;
import wsg.tools.boot.dao.jpa.mapper.JaAdultVideoRepository;
import wsg.tools.boot.pojo.entity.adult.JaAdultVideoEntity;
import wsg.tools.boot.pojo.entity.base.FailureReason;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.base.repository.LinkedRepoIterator;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.SiteUtils;
import wsg.tools.internet.info.adult.ggg.GggCategory;
import wsg.tools.internet.info.adult.ggg.GggGood;
import wsg.tools.internet.info.adult.ggg.GggGoodView;
import wsg.tools.internet.info.adult.ggg.GggPageReq;
import wsg.tools.internet.info.adult.ggg.GggSite;
import wsg.tools.internet.info.adult.licence.LicencePlateItem;
import wsg.tools.internet.info.adult.licence.LicencePlateSite;
import wsg.tools.internet.info.adult.midnight.MidnightAlbum;
import wsg.tools.internet.info.adult.midnight.MidnightColumn;
import wsg.tools.internet.info.adult.midnight.MidnightPageReq;
import wsg.tools.internet.info.adult.midnight.MidnightSite;
import wsg.tools.internet.info.adult.view.JaAdultEntry;
import wsg.tools.internet.info.adult.west.PornTubeSite;
import wsg.tools.internet.info.adult.wiki.CelebrityWikiSite;
import wsg.tools.internet.info.adult.wiki.WikiAdultEntry;
import wsg.tools.internet.info.adult.wiki.WikiCelebrity;
import wsg.tools.internet.info.adult.wiki.WikiCelebrityIndex;

/**
 * Tasks to import adult-related entities.
 *
 * @author Kingen
 * @since 2021/4/16
 */
@Slf4j
@Service
public class AdultScheduler {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.EPOCH;
    private static final LocalDateTime DEFAULT_START_TIME = LocalDate.EPOCH.atStartOfDay();

    private final SiteManager manager;
    private final JaAdultVideoRepository jaVideoRepository;
    private final AdultService adultService;
    private final TransactionTemplate template;

    @Autowired
    public AdultScheduler(SiteManager manager, JaAdultVideoRepository jaVideoRepository,
        AdultService adultService, TransactionTemplate template) {
        this.manager = manager;
        this.jaVideoRepository = jaVideoRepository;
        this.adultService = adultService;
        this.template = template;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void importPornTubeSite() {
        PornTubeSite site = manager.pornTubeSite();
        try {
            adultService.importIntListRepository(site.getHostname(), 0, site.getRepository());
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
            throw new AppException(e);
        }
    }

    @Scheduled(cron = "0 0 11 * * ?")
    public void importLicencePlate() {
        LicencePlateSite site = manager.licencePlateSite();
        LinkedRepository<String, LicencePlateItem> repository = site.getRepository();
        String domain = site.getHostname();
        int subtype = 0;
        try {
            Optional<String> last = jaVideoRepository.getFirstOrderUpdateTime(domain, subtype);
            LinkedRepoIterator<String, LicencePlateItem> iterator;
            if (last.isPresent()) {
                iterator = repository.linkedRepoIterator(last.get());
                iterator.next();
            } else {
                iterator = repository.linkedRepoIterator();
            }
            BatchResult<String> result = BatchResult.create();
            while (iterator.hasNext()) {
                LicencePlateItem item = iterator.next();
                Source source = Source.of(domain, subtype, item.getId(), item);
                Optional<FailureReason> reason = adultService.saveJaAdultEntry(item, source);
                if (reason.isPresent()) {
                    result.fail(item.getSerialNum(), reason.get().getText());
                } else {
                    result.succeed();
                }
            }
            log.info("Imported adult entries from {}:", domain);
            result.print(Function.identity());
        } catch (OtherResponseException | NotFoundException e) {
            log.error(e.getMessage());
            throw new AppException(e);
        }
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void importMidnightAmateur() {
        MidnightSite site = manager.midnightSite();
        String domain = site.getHostname();
        for (MidnightColumn column : MidnightColumn.amateurs()) {
            importMidnight(domain, column, site, id -> site.findAmateurEntry(column, id));
        }
    }

    @Scheduled(cron = "0 30 17 * * ?")
    public void importMidnightFormally() {
        MidnightColumn column = MidnightColumn.ENTRY;
        MidnightSite site = manager.midnightSite();
        importMidnight(site.getHostname(), column, site, site::findFormalEntry);
    }

    private <T extends MidnightAlbum & JaAdultEntry> void importMidnight(String domain,
        MidnightColumn column, MidnightSite site, RepoRetrievable<Integer, T> retrievable) {
        int subtype = column.getId();
        Optional<LocalDateTime> timeOp = jaVideoRepository.getLatestTimestamp(domain, subtype);
        LocalDateTime startTime = timeOp.orElse(DEFAULT_START_TIME);
        MidnightPageReq firstReq = MidnightPageReq.first(column);
        try {
            Deque<T> ts = new LinkedList<>();
            SiteUtils.forEachPageUntil(site, firstReq, Constants.emptyConsumer(), index -> {
                T t = null;
                try {
                    t = retrievable.findById(index.getId());
                } catch (NotFoundException | OtherResponseException e) {
                    throw new AppException(e);
                }
                if (t.getUpdate().isBefore(startTime)) {
                    return true;
                }
                ts.addLast(t);
                return false;
            });
            if (ts.isEmpty()) {
                return;
            }
            Set<Source> exists = Collections.emptySet();
            if (timeOp.isPresent()) {
                exists = findSourcesByTime(domain, subtype, startTime);
            }
            BatchResult<Integer> result = BatchResult.create();
            // items updated at the start time
            while (!ts.isEmpty()) {
                T t = ts.removeLast();
                int id = t.getId();
                Source source = Source.of(domain, subtype, id, t);
                if (exists.contains(source)) {
                    continue;
                }
                Optional<FailureReason> reason = adultService.saveJaAdultEntry(t, source);
                if (reason.isPresent()) {
                    result.fail(id, reason.get());
                } else {
                    result.succeed();
                }
                if (t.getUpdate().isAfter(startTime)) {
                    break;
                }
            }
            // items updated after the start time
            while (!ts.isEmpty()) {
                T t = ts.removeLast();
                int id = t.getId();
                Source source = Source.of(domain, subtype, id, t);
                Optional<FailureReason> reason = adultService.saveJaAdultEntry(t, source);
                if (reason.isPresent()) {
                    result.fail(id, reason.get());
                } else {
                    result.succeed();
                }
            }
            log.info("Imported {} entries from {}:", column, domain);
            result.print(String::valueOf);
        } catch (NotFoundException | OtherResponseException e) {
            log.error(e.getMessage());
            throw new AppException(e);
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void importGgg() {
        GggSite site = manager.gggSite();
        String domain = site.getHostname();
        for (GggCategory category : GggCategory.all()) {
            int subtype = category.getCode();
            Optional<LocalDateTime> timeOp = jaVideoRepository.getLatestTimestamp(domain, subtype);
            LocalDate startDate = timeOp.map(LocalDateTime::toLocalDate).orElse(DEFAULT_START_DATE);
            GggPageReq firstReq = GggPageReq.byDate(category);
            try {
                Deque<GggGood> goods = new LinkedList<>();
                SiteUtils.forEachPageUntil(site, firstReq, goods::addLast,
                    good -> good.getUpdate().isBefore(startDate));
                if (goods.isEmpty()) {
                    continue;
                }
                Set<Source> exists = Collections.emptySet();
                if (timeOp.isPresent()) {
                    exists = findSourcesByTime(domain, subtype, timeOp.get());
                }
                BatchResult<Integer> result = BatchResult.create();
                // goods updated at the start date
                while (!goods.isEmpty()) {
                    GggGood good = goods.removeLast();
                    int id = good.getId();
                    Source source = Source.of(domain, subtype, id, good);
                    if (exists.contains(source)) {
                        continue;
                    }
                    GggGoodView view = site.findById(id);
                    GggGoodAdapter adt = new GggGoodAdapter(good, view);
                    Optional<FailureReason> reason = adultService.saveJaAdultEntry(adt, source);
                    if (reason.isPresent()) {
                        result.fail(id, reason.get());
                    } else {
                        result.succeed();
                    }
                    if (good.getUpdate().isAfter(startDate)) {
                        break;
                    }
                }
                // goods updated after the start date
                while (!goods.isEmpty()) {
                    GggGood good = goods.removeLast();
                    int id = good.getId();
                    Source source = Source.of(domain, subtype, id, good);
                    GggGoodView view = site.findById(id);
                    GggGoodAdapter adapter = new GggGoodAdapter(good, view);
                    Optional<FailureReason> reason = adultService.saveJaAdultEntry(adapter, source);
                    if (reason.isPresent()) {
                        result.fail(id, reason.get());
                    } else {
                        result.succeed();
                    }
                }
                log.info("Imported entries of {} from {}:", category.getCode(), domain);
                result.print(String::valueOf);
            } catch (NotFoundException | OtherResponseException e) {
                log.error(e.getMessage());
                throw new AppException(e);
            }
        }
    }

    private Set<Source> findSourcesByTime(String domain, int subtype, LocalDateTime timestamp) {
        Source source = new Source();
        source.setDomain(domain);
        source.setSubtype(subtype);
        source.setTimestamp(timestamp);
        JaAdultVideoEntity probe = new JaAdultVideoEntity();
        probe.setSource(source);
        return jaVideoRepository.findAll(Example.of(probe)).stream()
            .map(JaAdultVideoEntity::getSource).collect(Collectors.toSet());
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void importCelebrityWiki() {
        CelebrityWikiSite site = manager.celebrityWikiSite();
        String domain = site.getHostname();
        // the id of a celebrity as the subtype of her works
        int startCelebrityId = jaVideoRepository.getMaxSubtype(domain).orElse(1);
        try {
            List<WikiCelebrityIndex> indices = site.findAllCelebrityIndices();
            // the remaining celebrities, sorted by id
            Iterator<WikiCelebrityIndex> iterator = indices.stream()
                .filter(index -> index.getId() > startCelebrityId)
                .sorted(Comparator.comparing(WikiCelebrityIndex::getId)).iterator();
            if (!iterator.hasNext()) {
                return;
            }
            BatchResult<Integer> result = BatchResult.create();
            while (iterator.hasNext()) {
                WikiCelebrityIndex index = iterator.next();
                WikiCelebrity celebrity;
                try {
                    celebrity = site.findById(index);
                } catch (NotFoundException e) {
                    result.fail(index.getId(), "Not Found");
                    continue;
                }
                Set<String> works = celebrity.getWorks();
                if (works.isEmpty()) {
                    continue;
                }
                int id = index.getId();
                CelebrityAdapter adapter = new CelebrityAdapter(celebrity, site::findAlbum);
                Source celSrc = Source.of(domain, 0, id, null);
                template.execute(status -> {
                    Optional<FailureReason> reason = adultService
                        .saveJaAdultActress(adapter, celSrc);
                    if (reason.isPresent()) {
                        result.fail(id, reason.get());
                        return null;
                    }
                    int count = 0;
                    for (String work : works) {
                        WikiAdultEntry entry = null;
                        try {
                            entry = site.findAdultEntry(work);
                        } catch (NotFoundException e) {
                            continue;
                        } catch (OtherResponseException e) {
                            // rollback
                            throw new AppException(e);
                        }
                        Source source = Source.of(domain, id, count, null);
                        adultService.saveJaAdultEntry(entry, source);
                        count++;
                    }
                    result.succeed();
                    return null;
                });
            }
            log.info("Imported adult entries from {}:", domain);
            result.print(String::valueOf);
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
            throw new AppException(e);
        }
    }
}
