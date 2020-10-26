package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.AbstractKeyValue;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.util.ServiceUtil;
import wsg.tools.boot.dao.api.VideoAdapter;
import wsg.tools.boot.dao.jpa.mapper.*;
import wsg.tools.boot.pojo.base.AppException;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.entity.*;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.StringUtilsExt;
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
import java.util.regex.Pattern;
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

    private final VideoAdapter adapter;
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;
    private final UserRecordRepository userRecordRepository;

    public SubjectServiceImpl(
            VideoAdapter adapter, MovieRepository movieRepository, SeriesRepository seriesRepository,
            SeasonRepository seasonRepository, EpisodeRepository episodeRepository,
            UserRecordRepository userRecordRepository) {
        this.adapter = adapter;
        this.movieRepository = movieRepository;
        this.seriesRepository = seriesRepository;
        this.seasonRepository = seasonRepository;
        this.episodeRepository = episodeRepository;
        this.userRecordRepository = userRecordRepository;
    }

    @Override
    public GenericResult<Long> insertSubjectByDb(long dbId) throws AppException {
        Optional<IdView> optional = movieRepository.findByDbId(dbId);
        if (optional.isPresent()) {
            return GenericResult.of(optional.get().getId());
        }
        optional = seasonRepository.findByDbId(dbId);
        if (optional.isPresent()) {
            return GenericResult.of(optional.get().getId());
        }

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
            return new GenericResult<>("Can't save series without IMDb id.");
        }

        BaseImdbTitle imdbTitle = adapter.imdbTitle(imdbId).get();
        return insertSeries(imdbId, imdbTitle);
    }

    @Override
    public GenericResult<Long> insertSubjectByImdb(String imdbId) throws AppException {
        GenericResult<BaseImdbTitle> result = adapter.imdbTitle(imdbId);
        if (!result.isSuccess()) {
            return new GenericResult<>(result.error());
        }
        BaseImdbTitle imdbTitle = result.get();
        if (imdbTitle instanceof ImdbMovie) {
            Optional<IdView> optional = movieRepository.findByImdbId(imdbId);
            if (optional.isPresent()) {
                return GenericResult.of(optional.get().getId());
            }

            Long dbId = adapter.getDbIdByImdbId(imdbId).orElse(null);
            return insertMovie(dbId, imdbId);
        }

        return insertSeries(imdbId, imdbTitle);
    }

    /**
     * At least one of the two ids is provided.
     */
    private GenericResult<Long> insertMovie(Long dbId, String imdbId) {
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
                addAll(durations, ((DoubanMovie) subject).getExtDurations());
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
        if (!durations.isEmpty()) {
            movieEntity.setDurations(durations.stream().sorted().collect(Collectors.toList()));
        }
        return GenericResult.of(movieRepository.insert(movieEntity).getId());
    }

    /**
     * Id and title are both required not null.
     */
    private GenericResult<Long> insertSeries(String imdbId, BaseImdbTitle title) throws AppException {
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

        Optional<IdView> optional = seriesRepository.findByImdbId(seriesImdbId);
        if (optional.isPresent()) {
            return GenericResult.of(optional.get().getId());
        }

        SeriesEntity seriesEntity = new SeriesEntity();
        seriesEntity.setImdbId(seriesImdbId);
        seriesEntity.setText(imdbSeries.getText());
        seriesEntity.setYear(imdbSeries.getYearInfo().getStart());
        seriesEntity.setLanguages(imdbSeries.getLanguages());
        if (imdbSeries.getRuntimes() != null) {
            seriesEntity.setDurations(imdbSeries.getRuntimes().stream().sorted().collect(Collectors.toList()));
        }

        List<String[]> allEpisodes = adapter.episodes(seriesImdbId).get();
        int seasonsCount = allEpisodes.size();
        if (seasonsCount == 0) {
            seasonsCount = 1;
        }
        seriesEntity.setSeasonsCount(seasonsCount);

        // get seasons and episodes
        Map<Integer, String> fails = new HashMap<>(4);
        List<SeasonEntity> seasons = new ArrayList<>();
        String[] season1Episodes = allEpisodes.isEmpty() ? new String[]{} : allEpisodes.get(0);
        getSeason(seriesImdbId, season1Episodes, 1)
                .ifPresentOr(seasons::add, msg -> fails.put(1, msg));

        if (allEpisodes.size() > 1) {
            for (int index = 1; index < allEpisodes.size(); index++) {
                String[] episodes = allEpisodes.get(index);
                String seasonImdbId = episodes[1];
                final int currentSeason = index + 1;
                if (seasonImdbId == null) {
                    fails.put(currentSeason, "None id of IMDb exists.");
                } else {
                    getSeason(seasonImdbId, episodes, currentSeason)
                            .ifPresentOr(seasons::add, msg -> fails.put(currentSeason, msg));
                }
            }
        }

        if (!fails.isEmpty()) {
            return new GenericResult<>(fails.entrySet().stream()
                    .map(entry -> String.format("Season: %d, error: %s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(Constants.LINE_SEPARATOR)));
        }
        try {
            seriesEntity.setTitle(extractTitle(seasons));
            SeriesEntity series = seriesRepository.insert(seriesEntity);
            for (SeasonEntity season : seasons) {
                season.setSeries(series);
                long seasonId = seasonRepository.insert(season).getId();
                for (EpisodeEntity episode : season.getEpisodes()) {
                    episode.setSeasonId(seasonId);
                    episodeRepository.insert(episode);
                }
            }
            return GenericResult.of(series.getId());
        } catch (DataIntegrityViolationException e) {
            return new GenericResult<>("Data of the series aren't integral: %s.", e.getMessage());
        } catch (IllegalArgumentException e) {
            return new GenericResult<>(e);
        }
    }

    private String extractTitle(List<SeasonEntity> seasons) {
        seasons.sort(Comparator.comparingInt(SeasonEntity::getCurrentSeason));
        String title = seasons.get(0).getTitle();
        final String first = " 第一季";
        if (title.endsWith(first)) {
            title = title.substring(0, title.length() - 4);
        }
        String encoded = StringUtilsExt.encodeAsPattern(title);
        for (SeasonEntity season : seasons) {
            Integer currentSeason = season.getCurrentSeason();
            if (currentSeason == 1) {
                continue;
            }
            String ji = "第" + StringUtilsExt.chineseNumeric(currentSeason) + "季";
            Pattern pattern = Pattern.compile(encoded + "(" + currentSeason + "[\u4E00-\u9FBF]*| " + ji + ")");
            AssertUtils.matches(pattern, season.getTitle());
        }
        return title;
    }

    /**
     * All args are required not null.
     */
    private GenericResult<SeasonEntity> getSeason(String seasonImdbId, String[] episodes, int currentSeason) {
        GenericResult<Long> idResult = adapter.getDbIdByImdbId(seasonImdbId);
        if (!idResult.isSuccess()) {
            return new GenericResult<>(idResult.error());
        }
        Long seasonDbId = idResult.get();
        GenericResult<BaseDoubanSubject> subjectResult = adapter.doubanSubject(seasonDbId);
        if (!subjectResult.isSuccess()) {
            return new GenericResult<>(subjectResult.error());
        }
        BaseDoubanSubject subject = subjectResult.get();

        SeasonEntity seasonEntity = new SeasonEntity();
        seasonEntity.setDbId(seasonDbId);
        seasonEntity.setTitle(subject.getTitle());
        seasonEntity.setOriginalTitle(subject.getOriginalTitle());
        seasonEntity.setYear(subject.getYear());
        seasonEntity.setLanguages(subject.getLanguages());
        if (subject.getDuration() != null) {
            seasonEntity.setDurations(Collections.singletonList(subject.getDuration()));
        }
        seasonEntity.setEpisodesCount(((DoubanSeries) subject).getEpisodesCount());
        if (seasonEntity.getEpisodesCount() == null || seasonEntity.getEpisodesCount() < 1) {
            seasonEntity.setEpisodesCount(episodes.length - 1);
        }
        seasonEntity.setCurrentSeason(currentSeason);

        List<EpisodeEntity> episodeEntities = new ArrayList<>();
        for (int i = 0; i < episodes.length; i++) {
            String episodeImdbId = episodes[i];
            if (StringUtils.isNotBlank(episodeImdbId)) {
                insertEpisode(episodeImdbId, i)
                        .ifPresentOrThrows(episodeEntities::add);
            }
        }
        seasonEntity.setEpisodes(episodeEntities);
        return GenericResult.of(seasonEntity);
    }

    private GenericResult<EpisodeEntity> insertEpisode(String episodeImdbId, int currentEpisode) {
        EpisodeEntity episodeEntity = new EpisodeEntity();
        episodeEntity.setImdbId(episodeImdbId);

        adapter.imdbTitle(episodeImdbId).ifPresentOrThrows(imdbTitle -> {
            episodeEntity.setText(imdbTitle.getText());
            episodeEntity.setReleased(imdbTitle.getRelease());
            if (imdbTitle.getRuntimes() != null) {
                episodeEntity.setDurations(imdbTitle.getRuntimes().stream().sorted().collect(Collectors.toList()));
            }
        });

        episodeEntity.setCurrentEpisode(currentEpisode);
        return GenericResult.of(episodeEntity);
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
                    insertSubjectByDb(entry.getKey()).ifPresentOr(
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
                ids, entry -> insertSubjectByImdb(entry.getKey()),
                AbstractKeyValue::getKey
        );
    }
}
