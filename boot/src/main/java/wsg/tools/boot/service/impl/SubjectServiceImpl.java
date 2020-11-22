package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import wsg.tools.boot.dao.api.intf.SubjectAdapter;
import wsg.tools.boot.dao.jpa.mapper.*;
import wsg.tools.boot.pojo.entity.UserRecordEntity;
import wsg.tools.boot.pojo.entity.base.IdView;
import wsg.tools.boot.pojo.entity.subject.EpisodeEntity;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.error.DataIntegrityException;
import wsg.tools.boot.pojo.error.SiteException;
import wsg.tools.boot.pojo.error.UnexpectedException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.pojo.result.BiResult;
import wsg.tools.boot.pojo.result.ListResult;
import wsg.tools.boot.pojo.result.SingleResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;
import wsg.tools.internet.video.entity.douban.object.DoubanMovie;
import wsg.tools.internet.video.entity.douban.object.DoubanSeries;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.entity.imdb.object.ImdbEpisode;
import wsg.tools.internet.video.entity.imdb.object.ImdbMovie;
import wsg.tools.internet.video.entity.imdb.object.ImdbSeries;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.DoubanSite;

import javax.annotation.Nonnull;
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

    private final SubjectAdapter adapter;
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;
    private final UserRecordRepository userRecordRepository;

    public SubjectServiceImpl(
            SubjectAdapter adapter, MovieRepository movieRepository, SeriesRepository seriesRepository,
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
    public SingleResult<Long> insertSubjectByDb(long dbId) throws NotFoundException, SiteException, DataIntegrityException {
        Optional<IdView<Long>> optional = movieRepository.findByDbId(dbId);
        if (optional.isPresent()) {
            return SingleResult.of(optional.get().getId());
        }
        optional = seasonRepository.findByDbId(dbId);
        if (optional.isPresent()) {
            return SingleResult.of(optional.get().getId());
        }

        BaseDoubanSubject subject = adapter.doubanSubject(dbId).getRecord();
        String imdbId = subject.getImdbId();
        if (subject instanceof DoubanMovie) {
            return insertMovie(dbId, imdbId);
        }

        if (imdbId == null) {
            throw new NotFoundException("Can't save series without IMDb id.");
        }

        BaseImdbTitle imdbTitle;
        try {
            imdbTitle = adapter.imdbTitle(imdbId).getRecord();
        } catch (NotFoundException e) {
            throw new UnexpectedException(e);
        }
        return insertSeries(imdbId, imdbTitle);
    }

    @Override
    public SingleResult<Long> insertSubjectByImdb(String imdbId) throws NotFoundException, SiteException, DataIntegrityException {
        BaseImdbTitle imdbTitle = adapter.imdbTitle(imdbId).getRecord();
        if (imdbTitle instanceof ImdbMovie) {
            Optional<IdView<Long>> optional = movieRepository.findByImdbId(imdbId);
            if (optional.isPresent()) {
                return SingleResult.of(optional.get().getId());
            }

            try {
                return insertMovie(adapter.getDbIdByImdbId(imdbId).getRecord(), imdbId);
            } catch (SiteException | NotFoundException ignored) {
                return insertMovie(null, imdbId);
            }
        }

        return insertSeries(imdbId, imdbTitle);
    }

    private SingleResult<Long> insertMovie(Long dbId, String imdbId) {
        if (Objects.isNull(dbId) && StringUtils.isBlank(imdbId)) {
            throw new IllegalArgumentException("At least one valid identifier should be provided.");
        }

        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setDbId(dbId);
        movieEntity.setImdbId(imdbId);
        Set<Duration> durations = new HashSet<>();

        if (dbId != null) {
            try {
                BaseDoubanSubject subject = adapter.doubanSubject(dbId).getRecord();
                movieEntity.setTitle(subject.getTitle());
                movieEntity.setOriginalTitle(subject.getOriginalTitle());
                movieEntity.setYear(subject.getYear());
                movieEntity.setLanguages(subject.getLanguages());
                CollectionUtils.addIgnoreNull(durations, subject.getDuration());
                List<Duration> extDurations = ((DoubanMovie) subject).getExtDurations();
                if (extDurations != null) {
                    durations.addAll(extDurations);
                }
            } catch (NotFoundException e) {
                log.error(e.getMessage());
            }
        }
        if (StringUtils.isNotBlank(imdbId)) {
            try {
                BaseImdbTitle imdbTitle = adapter.imdbTitle(imdbId).getRecord();
                movieEntity.setText(imdbTitle.getText());
                if (movieEntity.getYear() == null) {
                    movieEntity.setYear(((ImdbMovie) imdbTitle).getYear());
                }
                if (movieEntity.getLanguages() == null) {
                    movieEntity.setLanguages(imdbTitle.getLanguages());
                }
                if (imdbTitle.getRuntimes() != null) {
                    durations.addAll(imdbTitle.getRuntimes());
                }
            } catch (NotFoundException e) {
                throw new UnexpectedException(e);
            }
        }
        if (!durations.isEmpty()) {
            movieEntity.setDurations(durations.stream().sorted().collect(Collectors.toList()));
        }
        return SingleResult.of(movieRepository.insert(movieEntity).getId());
    }

    /**
     * Id and title are both required not null.
     */
    private SingleResult<Long> insertSeries(@Nonnull String imdbId, @Nonnull BaseImdbTitle title)
            throws SiteException, DataIntegrityException {
        String seriesImdbId;
        ImdbSeries imdbSeries;
        if (title instanceof ImdbEpisode) {
            seriesImdbId = ((ImdbEpisode) title).getSeriesId();
            try {
                imdbSeries = (ImdbSeries) adapter.imdbTitle(seriesImdbId).getRecord();
            } catch (NotFoundException e) {
                throw new UnexpectedException(e);
            }
        } else if (title instanceof ImdbSeries) {
            seriesImdbId = imdbId;
            imdbSeries = (ImdbSeries) title;
        } else {
            throw new SiteException("IMDb", String.format("Unexpected type %s for title %s.", title.getClass().getSimpleName(), imdbId));
        }

        Optional<IdView<Long>> optional = seriesRepository.findByImdbId(seriesImdbId);
        if (optional.isPresent()) {
            return SingleResult.of(optional.get().getId());
        }

        SeriesEntity seriesEntity = new SeriesEntity();
        seriesEntity.setImdbId(seriesImdbId);
        seriesEntity.setText(imdbSeries.getText());
        seriesEntity.setYear(imdbSeries.getYearInfo().getStart());
        seriesEntity.setLanguages(imdbSeries.getLanguages());
        if (imdbSeries.getRuntimes() != null) {
            seriesEntity.setDurations(imdbSeries.getRuntimes().stream().sorted().collect(Collectors.toList()));
        }

        List<String[]> allEpisodes;
        try {
            allEpisodes = adapter.episodes(seriesImdbId).getRecord();
        } catch (NotFoundException e) {
            throw new UnexpectedException(e);
        }
        int seasonsCount = allEpisodes.size();
        if (seasonsCount == 0) {
            seasonsCount = 1;
        }
        seriesEntity.setSeasonsCount(seasonsCount);

        // get seasons and episodes
        Map<Integer, String> fails = new HashMap<>(4);
        List<BiResult<SeasonEntity, List<EpisodeEntity>>> seasons = new ArrayList<>();
        String[] season1Episodes = allEpisodes.isEmpty() ? new String[]{} : allEpisodes.get(0);
        try {
            seasons.add(getSeason(seriesImdbId, season1Episodes, 1));
        } catch (NotFoundException | SiteException e) {
            fails.put(1, e.getMessage());
        }

        if (allEpisodes.size() > 1) {
            for (int index = 1; index < allEpisodes.size(); index++) {
                String[] episodes = allEpisodes.get(index);
                String seasonImdbId = episodes[1];
                final int currentSeason = index + 1;
                if (seasonImdbId == null) {
                    fails.put(currentSeason, "None id of IMDb exists.");
                } else {
                    try {
                        seasons.add(getSeason(seasonImdbId, episodes, currentSeason));
                    } catch (NotFoundException | SiteException e) {
                        fails.put(currentSeason, e.getMessage());
                    }
                }
            }
        }

        if (!fails.isEmpty()) {
            throw new DataIntegrityException(fails.entrySet().stream()
                    .map(entry -> String.format("Season: %d, error: %s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(Constants.LINE_SEPARATOR)));
        }
        try {
            seriesEntity.setTitle(extractTitle(seasons.stream().map(BiResult::getLeft).collect(Collectors.toList())));
            SeriesEntity series = seriesRepository.insert(seriesEntity);
            for (BiResult<SeasonEntity, List<EpisodeEntity>> result : seasons) {
                SeasonEntity season = result.getLeft();
                season.setSeries(series);
                long seasonId = seasonRepository.insert(season).getId();
                for (EpisodeEntity episode : result.getRight()) {
                    episode.setSeasonId(seasonId);
                    episodeRepository.insert(episode);
                }
            }
            return SingleResult.of(series.getId());
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Data of the series aren't integral: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new DataIntegrityException(e.getMessage());
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
            RegexUtils.matchesOrElseThrow(pattern, season.getTitle());
        }
        return title;
    }

    /**
     * All args are required not null.
     */
    private BiResult<SeasonEntity, List<EpisodeEntity>> getSeason(String seasonImdbId, String[] episodes, int currentSeason)
            throws NotFoundException, SiteException {
        Long seasonDbId = adapter.getDbIdByImdbId(seasonImdbId).getRecord();
        BaseDoubanSubject subject = adapter.doubanSubject(seasonDbId).getRecord();

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
                episodeEntities.add(getEpisode(episodeImdbId, i));
            }
        }
        return BiResult.of(seasonEntity, episodeEntities);
    }

    private EpisodeEntity getEpisode(String episodeImdbId, int currentEpisode) {
        EpisodeEntity episodeEntity = new EpisodeEntity();
        episodeEntity.setImdbId(episodeImdbId);

        BaseImdbTitle imdbTitle;
        try {
            imdbTitle = adapter.imdbTitle(episodeImdbId).getRecord();
        } catch (NotFoundException e) {
            throw new UnexpectedException(e);
        }
        episodeEntity.setText(imdbTitle.getText());
        episodeEntity.setReleased(imdbTitle.getRelease());
        if (imdbTitle.getRuntimes() != null) {
            episodeEntity.setDurations(imdbTitle.getRuntimes().stream().sorted().collect(Collectors.toList()));
        }
        episodeEntity.setCurrentEpisode(currentEpisode);
        return episodeEntity;
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
            Map<Long, LocalDate> map;
            try {
                map = adapter.collectUserSubjects(userId, since, mark).getRecord();
            } catch (NotFoundException e) {
                log.error(e.getMessage());
                continue;
            }
            for (Map.Entry<Long, LocalDate> entry : map.entrySet()) {
                Long id;
                try {
                    id = insertSubjectByDb(entry.getKey()).getRecord();
                } catch (SiteException | DataIntegrityException | NotFoundException e) {
                    fails.put(entry.getKey(), e.getMessage());
                    continue;
                }
                count[0]++;
                UserRecordEntity entity = new UserRecordEntity();
                entity.setMark(mark);
                entity.setMarkDate(entry.getValue());
                entity.setSubjectId(id);
                entity.setUserId(userId);
                userRecordRepository.updateOrInsert(entity, () -> userRecordRepository.findBySubjectIdAndUserId(id, userId));
            }
        }
        return new BatchResult<>(count[0], fails);
    }

    @Override
    public ListResult<MovieEntity> listMovies() {
        return ListResult.of(movieRepository.findAll());
    }

    @Override
    public Optional<MovieEntity> getMovie(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public ListResult<SeriesEntity> listSeries() {
        return ListResult.of(seriesRepository.findAll());
    }

    @Override
    public Optional<SeriesEntity> getSeries(Long id) {
        return seriesRepository.findById(id);
    }

    @Override
    public List<SeasonEntity> getSeasonsBySeries(Long seriesId) {
        return seasonRepository.findAllBySeriesId(seriesId);
    }
}
