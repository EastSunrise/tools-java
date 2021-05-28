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
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.dao.api.adapter.impl.CelebrityAdapter;
import wsg.tools.boot.dao.api.adapter.impl.MidnightEntryAdapter;
import wsg.tools.boot.dao.api.adapter.impl.PornTubeVideoAdapter;
import wsg.tools.boot.dao.api.adapter.impl.WikiEntryAdapter;
import wsg.tools.boot.dao.api.support.SiteManager;
import wsg.tools.boot.dao.jpa.mapper.JaAdultVideoRepository;
import wsg.tools.boot.dao.jpa.mapper.WesternAdultVideoRepository;
import wsg.tools.boot.pojo.entity.adult.JaAdultVideoEntity;
import wsg.tools.boot.pojo.entity.base.FailureReason;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.intf.AdultService;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.base.page.PageIndex;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.SiteUtils;
import wsg.tools.internet.info.adult.midnight.MidnightColumn;
import wsg.tools.internet.info.adult.midnight.MidnightEntry;
import wsg.tools.internet.info.adult.midnight.MidnightSite;
import wsg.tools.internet.info.adult.west.PornTubeSite;
import wsg.tools.internet.info.adult.west.PornTubeVideo;
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

    private static final LocalDateTime DEFAULT_START_TIME = LocalDate.EPOCH.atStartOfDay();

    private final SiteManager manager;
    private final JaAdultVideoRepository jaVideoRepository;
    private final WesternAdultVideoRepository wtVideoRepository;
    private final AdultService adultService;
    private final TransactionTemplate template;

    @Autowired
    public AdultScheduler(SiteManager manager, JaAdultVideoRepository jaVideoRepository,
        WesternAdultVideoRepository wtVideoRepository,
        AdultService adultService, TransactionTemplate template) {
        this.manager = manager;
        this.jaVideoRepository = jaVideoRepository;
        this.wtVideoRepository = wtVideoRepository;
        this.adultService = adultService;
        this.template = template;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void importPornTubeSite() {
        PornTubeSite site = manager.pornTubeSite();
        String sname = site.getName();
        int subtype = 0;
        long startId = wtVideoRepository.getMaxRid(sname, subtype).orElse(0L);
        try {
            List<Integer> ids = site.findAllVideoIndices().keySet().stream()
                .filter(id -> id > startId).sorted().collect(Collectors.toList());
            BatchResult<Integer> result = BatchResult.create();
            for (int id : ids) {
                PornTubeVideo video = site.findById(id);
                PornTubeVideoAdapter adapter = new PornTubeVideoAdapter(video);
                Source source = Source.of(sname, subtype, id, video);
                Optional<FailureReason> rs = adultService.saveWesternAdultEntry(adapter, source);
                if (rs.isPresent()) {
                    result.fail(id, rs.get());
                } else {
                    result.succeed();
                }
            }
            log.info("Imported adult entries from {}:", sname);
            if (result.hasFailures()) {
                log.error(result.toString());
            } else {
                log.info(result.toString());
            }
        } catch (OtherResponseException | NotFoundException e) {
            log.error(e.getMessage());
            throw new AppException(e);
        }
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void importMidnightAmateur() {
        MidnightSite site = manager.midnightSite();
        String sname = site.getName();
        for (MidnightColumn column : MidnightColumn.amateurs()) {
            importMidnight(sname, column, site, id -> site.findAmateurEntry(column, id));
        }
    }

    @Scheduled(cron = "0 30 17 * * ?")
    public void importMidnightFormally() {
        MidnightSite site = manager.midnightSite();
        importMidnight(site.getName(), MidnightColumn.ENTRY, site, site::findFormalEntry);
    }

    private <T extends MidnightEntry> void importMidnight(String sname,
        MidnightColumn column, MidnightSite site, RepoRetrievable<Integer, T> retrievable) {
        int subtype = column.getId();
        Optional<LocalDateTime> timeOp = jaVideoRepository.getLatestTimestamp(sname, subtype);
        LocalDateTime startTime = timeOp.orElse(DEFAULT_START_TIME);
        try {
            Deque<T> ts = new LinkedList<>();
            SiteUtils
                .forEachPageUntil(pageIndex -> site.findAll(pageIndex, column), PageIndex.first(),
                    Constants.emptyConsumer(), index -> {
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
                exists = findSourcesByTime(sname, subtype, startTime);
            }
            BatchResult<Integer> result = BatchResult.create();
            // items updated at the start time
            while (!ts.isEmpty()) {
                MidnightEntryAdapter adapter = new MidnightEntryAdapter(ts.removeLast());
                int id = adapter.getId();
                Source source = Source.of(sname, subtype, id, adapter);
                if (exists.contains(source)) {
                    continue;
                }
                Optional<FailureReason> reason = adultService.saveJaAdultEntry(adapter, source);
                if (reason.isPresent()) {
                    result.fail(id, reason.get());
                } else {
                    result.succeed();
                }
                if (adapter.getUpdate().isAfter(startTime)) {
                    break;
                }
            }
            // items updated after the start time
            while (!ts.isEmpty()) {
                MidnightEntryAdapter adapter = new MidnightEntryAdapter(ts.removeLast());
                int id = adapter.getId();
                Source source = Source.of(sname, subtype, id, adapter);
                Optional<FailureReason> reason = adultService.saveJaAdultEntry(adapter, source);
                if (reason.isPresent()) {
                    result.fail(id, reason.get());
                } else {
                    result.succeed();
                }
            }
            log.info("Imported {} entries from {}:", column, sname);
            if (result.hasFailures()) {
                log.error(result.toString());
            } else {
                log.info(result.toString());
            }
        } catch (NotFoundException | OtherResponseException e) {
            log.error(e.getMessage());
            throw new AppException(e);
        }
    }

    private Set<Source> findSourcesByTime(String sname, int subtype, LocalDateTime timestamp) {
        Source source = new Source();
        source.setSname(sname);
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
        String sname = site.getName();
        // the id of a celebrity as the subtype of her works
        int startCelebrityId = jaVideoRepository.getMaxSubtype(sname).orElse(1);
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
                Source celSrc = Source.of(sname, 0, id, null);
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
                        Source source = Source.of(sname, id, count, null);
                        adultService.saveJaAdultEntry(new WikiEntryAdapter(entry), source);
                        count++;
                    }
                    result.succeed();
                    return null;
                });
            }
            log.info("Imported adult entries from {}:", sname);
            if (result.hasFailures()) {
                log.error(result.toString());
            } else {
                log.info(result.toString());
            }
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
            throw new AppException(e);
        }
    }
}
