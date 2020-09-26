package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.AbstractKeyValue;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.common.util.ServiceUtil;
import wsg.tools.boot.dao.api.VideoAdapter;
import wsg.tools.boot.dao.jpa.mapper.*;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.entity.*;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;
import wsg.tools.internet.video.entity.douban.object.DoubanMovie;
import wsg.tools.internet.video.entity.douban.object.DoubanSeries;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.entity.imdb.object.ImdbEpisode;
import wsg.tools.internet.video.entity.imdb.object.ImdbMovie;
import wsg.tools.internet.video.entity.imdb.object.ImdbSeries;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.DoubanSite;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implement of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Slf4j
@Service
public class SubjectServiceImpl extends BaseServiceImpl implements SubjectService {

    private VideoAdapter adapter;
    private MovieRepository movieRepository;
    private SeriesRepository seriesRepository;
    private SeasonRepository seasonRepository;
    private EpisodeRepository episodeRepository;
    private UserRecordRepository userRecordRepository;
    private TransactionTemplate template;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public GenericResult<Long> insertSubjectByDb(long dbId) {
        GenericResult<BaseDoubanSubject> subjectResult = adapter.doubanSubject(dbId);
        if (!subjectResult.isSuccess()) {
            return new GenericResult<>(subjectResult.error());
        }

        BaseDoubanSubject subject = subjectResult.get();
        String imdbId = subject.getImdbId();

        if (subject instanceof DoubanMovie) {
            return insertMovie(dbId, imdbId);
        }

        if (imdbId == null) {
            return new GenericResult<>("Can't save series %d without IMDb id.", dbId);
        }

        BaseImdbTitle imdbTitle = adapter.imdbTitle(imdbId).get();
        return insertSeries(imdbId, imdbTitle);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public GenericResult<Long> insertSubjectByImdb(String imdbId, Long dbId) {
        GenericResult<BaseImdbTitle> result = adapter.imdbTitle(imdbId);
        if (!result.isSuccess()) {
            return new GenericResult<>(result.error());
        }
        BaseImdbTitle imdbTitle = result.get();
        if (imdbTitle instanceof ImdbMovie) {
            if (dbId == null) {
                dbId = adapter.getDbIdByImdbId(imdbId).orElse(null);
            }
            return insertMovie(dbId, imdbId);
        }

        return insertSeries(imdbId, imdbTitle);
    }

    /**
     * At least one of the two ids is provided.
     */
    private GenericResult<Long> insertMovie(Long dbId, String imdbId) {
        if (dbId != null) {
            Optional<IdSupplier> optional = movieRepository.findByDbId(dbId);
            if (optional.isPresent()) {
                return GenericResult.of(optional.get().getId());
            }
        }
        if (imdbId != null) {
            Optional<IdSupplier> optional = movieRepository.findByImdbId(imdbId);
            if (optional.isPresent()) {
                return GenericResult.of(optional.get().getId());
            }
        }
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setDbId(dbId);
        movieEntity.setImdbId(imdbId);
        Set<Duration> durations = new HashSet<>();

        if (dbId != null) {
            adapter.doubanSubject(dbId).ifPresentOrLog(subject -> {
                movieEntity.setTitle(subject.getTitle());
                movieEntity.setOriginalTitle(subject.getOriginalTitle());
                movieEntity.setYear(subject.getYear());
                movieEntity.setLanguages(subject.getLanguages());
                CollectionUtils.addIgnoreNull(durations, subject.getDuration());
                addAll(durations, subject.getExtDurations());
            }, log);
        }
        if (StringUtils.isNotBlank(imdbId)) {
            adapter.imdbTitle(imdbId).ifPresentOrThrows(imdbTitle -> {
                movieEntity.setText(imdbTitle.getText());
                if (movieEntity.getYear() == null) {
                    movieEntity.setYear(((ImdbMovie) imdbTitle).getYear());
                }
                if (movieEntity.getLanguages() == null) {
                    movieEntity.setLanguages(imdbTitle.getLanguages());
                }
                addAll(durations, imdbTitle.getRuntimes());
            });
        }
        movieEntity.setDurations(durations.stream().sorted().collect(Collectors.toList()));
        return GenericResult.of(movieRepository.insert(movieEntity).getId());
    }

    /**
     * Id and title are both required not null.
     */
    private GenericResult<Long> insertSeries(String imdbId, BaseImdbTitle title) {
        String seriesImdbId;
        ImdbSeries imdbSeries;
        if (title instanceof ImdbEpisode) {
            seriesImdbId = ((ImdbEpisode) title).getSeriesId();
            imdbSeries = (ImdbSeries) adapter.imdbTitle(seriesImdbId).get();
            if (seriesImdbId == null) {
                return new GenericResult<>("Can't get series id for episode %s.", imdbId);
            }
        } else if (title instanceof ImdbSeries) {
            seriesImdbId = imdbId;
            imdbSeries = (ImdbSeries) title;
        } else {
            return new GenericResult<>("Unexpected type %s for title %s.", title.getClass().getSimpleName(), imdbId);
        }

        Optional<IdSupplier> optional = seriesRepository.findByImdbId(seriesImdbId);
        if (optional.isPresent()) {
            return GenericResult.of(optional.get().getId());
        }

        SeriesEntity seriesEntity = new SeriesEntity();
        seriesEntity.setImdbId(seriesImdbId);
        Set<Duration> durations = new HashSet<>();
        seriesEntity.setText(imdbSeries.getText());
        seriesEntity.setYear(imdbSeries.getYearInfo().getStart());
        seriesEntity.setLanguages(imdbSeries.getLanguages());
        addAll(durations, imdbSeries.getRuntimes());
        seriesEntity.setDurations(durations.stream().sorted().collect(Collectors.toList()));

        List<String[]> allEpisodes = adapter.episodes(seriesImdbId).get();
        int seasonsCount = allEpisodes.size();
        if (seasonsCount == 0) {
            seasonsCount = 1;
        }
        seriesEntity.setSeasonsCount(seasonsCount);
        long seriesId = seriesRepository.insert(seriesEntity).getId();

        // insert seasons and episodes
        String[] season1Episodes = allEpisodes.isEmpty() ? new String[]{} : allEpisodes.get(0);
        insertSeason(seriesImdbId, season1Episodes, seriesId, 1);

        if (allEpisodes.size() > 1) {
            for (int i = 1; i < allEpisodes.size(); i++) {
                String[] episodes = allEpisodes.get(i);
                String seasonImdbId = episodes[1];
                if (seasonImdbId == null) {
                    log.error("None id of IMDb exists for season {} of series {}.", i + 1, seriesImdbId);
                    continue;
                }
                insertSeason(seasonImdbId, episodes, seriesId, i + 1);
            }
        }

        return GenericResult.of(seriesId);
    }

    /**
     * All args are required not null.
     */
    private void insertSeason(String seasonImdbId, String[] episodes, long seriesId, int currentSeason) {
        GenericResult<Long> idResult = adapter.getDbIdByImdbId(seasonImdbId);
        if (!idResult.isSuccess()) {
            log.error(idResult.error());
            return;
        }
        Long seasonDbId = idResult.get();

        SeasonEntity seasonEntity = new SeasonEntity();
        seasonEntity.setDbId(seasonDbId);
        Set<Duration> durations = new HashSet<>();
        adapter.doubanSubject(seasonDbId).ifPresentOrThrows(subject -> {
            seasonEntity.setTitle(subject.getTitle());
            seasonEntity.setOriginalTitle(subject.getOriginalTitle());
            seasonEntity.setYear(subject.getYear());
            seasonEntity.setLanguages(subject.getLanguages());
            CollectionUtils.addIgnoreNull(durations, subject.getDuration());
            addAll(durations, subject.getExtDurations());
            seasonEntity.setEpisodesCount(((DoubanSeries) subject).getEpisodesCount());
        });
        seasonEntity.setDurations(durations.stream().sorted().collect(Collectors.toList()));
        if (seasonEntity.getEpisodesCount() == null || seasonEntity.getEpisodesCount() < 1) {
            seasonEntity.setEpisodesCount(episodes.length - 1);
        }
        seasonEntity.setSeriesId(seriesId);
        seasonEntity.setCurrentSeason(currentSeason);
        long seasonId = seasonRepository.insert(seasonEntity).getId();

        for (int i = 0; i < episodes.length; i++) {
            String episodeImdbId = episodes[i];
            if (StringUtils.isNotBlank(episodeImdbId)) {
                insertEpisode(episodeImdbId, seasonId, i);
            }
        }
    }

    private void insertEpisode(String episodeImdbId, long seasonId, int currentEpisode) {
        EpisodeEntity episodeEntity = new EpisodeEntity();
        episodeEntity.setImdbId(episodeImdbId);
        Set<Duration> durations = new HashSet<>();

        adapter.imdbTitle(episodeImdbId).ifPresentOrThrows(imdbTitle -> {
            episodeEntity.setText(imdbTitle.getText());
            episodeEntity.setReleased(imdbTitle.getRelease());
            CollectionUtils.addIgnoreNull(durations, ((ImdbEpisode) imdbTitle).getDuration());
            addAll(durations, imdbTitle.getRuntimes());
        });

        episodeEntity.setDurations(durations.stream().sorted().collect(Collectors.toList()));
        episodeEntity.setCurrentEpisode(currentEpisode);
        episodeEntity.setSeasonId(seasonId);
        episodeRepository.insert(episodeEntity);
    }

    private <T> void addAll(Collection<T> target, Collection<T> added) {
        if (added != null) {
            target.addAll(added);
        }
    }

    @Override
    public BatchResult<Long> importDouban(long userId, LocalDate since) {
        if (since == null) {
            since = userRecordRepository.findMaxMarkDate();
            if (since == null) {
                since = DoubanSite.DOUBAN_START_DATE;
            }
        }
        log.info("Start to import douban subjects of {} since {}", userId, since);
        int[] count = {0};
        Map<Long, String> fails = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (MarkEnum mark : MarkEnum.values()) {
            adapter.collectUserSubjects(userId, since, mark).ifPresentOrLog(map -> {
                for (Map.Entry<Long, LocalDate> entry : map.entrySet()) {
                    Objects.requireNonNull(template.execute(status -> insertSubjectByDb(entry.getKey()))).ifPresentOr(
                            id -> {
                                count[0]++;
                                UserRecordEntity entity = new UserRecordEntity();
                                entity.setMark(mark);
                                entity.setMarkDate(entry.getValue());
                                entity.setSubjectId(id);
                                entity.setUserId(userId);
                                userRecordRepository.updateOrInsert(entity,
                                        () -> userRecordRepository.findBySubjectIdAndUserId(id, userId));
                            },
                            msg -> fails.put(entry.getKey(), msg)
                    );
                }
            }, log);
        }
        return new BatchResult<>(count[0], fails);
    }

    @Override
    public BatchResult<String> importManually(List<DefaultKeyValue<String, Long>> ids) {
        log.info("Start to import subjects manually.");
        return ServiceUtil.batch(
                ids, entry -> template.execute(status -> insertSubjectByImdb(entry.getKey(), entry.getValue())),
                AbstractKeyValue::getKey
        );
    }

    @Autowired
    public void setUserRecordRepository(UserRecordRepository userRecordRepository) {
        this.userRecordRepository = userRecordRepository;
    }

    @Autowired
    public void setMovieRepository(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Autowired
    public void setSeriesRepository(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    @Autowired
    public void setSeasonRepository(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    @Autowired
    public void setEpisodeRepository(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    @Autowired
    public void setTemplate(TransactionTemplate template) {
        this.template = template;
    }

    @Autowired
    public void setAdapter(VideoAdapter adapter) {
        this.adapter = adapter;
    }
}
