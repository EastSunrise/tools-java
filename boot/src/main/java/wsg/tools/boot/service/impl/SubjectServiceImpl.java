package wsg.tools.boot.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.dao.api.intf.ImdbEpisodeView;
import wsg.tools.boot.dao.api.intf.ImdbMovieView;
import wsg.tools.boot.dao.api.intf.ImdbSeriesView;
import wsg.tools.boot.dao.api.intf.ImdbView;
import wsg.tools.boot.dao.api.intf.SubjectAdapter;
import wsg.tools.boot.dao.jpa.mapper.EpisodeRepository;
import wsg.tools.boot.dao.jpa.mapper.MovieRepository;
import wsg.tools.boot.dao.jpa.mapper.SeasonRepository;
import wsg.tools.boot.dao.jpa.mapper.SeriesRepository;
import wsg.tools.boot.dao.jpa.mapper.UserRecordRepository;
import wsg.tools.boot.pojo.entity.UserRecordEntity;
import wsg.tools.boot.pojo.entity.base.IdView;
import wsg.tools.boot.pojo.entity.subject.EpisodeEntity;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.error.DataIntegrityException;
import wsg.tools.boot.pojo.error.ShouldExistException;
import wsg.tools.boot.pojo.error.UnknownTypeException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.pojo.result.SingleResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.movie.common.Runtime;
import wsg.tools.internet.movie.common.enums.DoubanMark;
import wsg.tools.internet.movie.douban.BaseDoubanSubject;
import wsg.tools.internet.movie.douban.DoubanMovie;
import wsg.tools.internet.movie.douban.DoubanSeries;
import wsg.tools.internet.movie.douban.DoubanSite;

/**
 * Implement of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Slf4j
@Service
public class SubjectServiceImpl extends BaseServiceImpl implements SubjectService {

    private static final String MOVIE_NOT_RELEASED_MSSG = "The movie isn't released yet.";

    @Setter(onMethod_ = {@Autowired})
    private SubjectAdapter adapter;

    @Setter(onMethod_ = {@Autowired})
    private MovieRepository movieRepository;

    @Setter(onMethod_ = {@Autowired})
    private SeriesRepository seriesRepository;

    @Setter(onMethod_ = {@Autowired})
    private SeasonRepository seasonRepository;

    @Setter(onMethod_ = {@Autowired})
    private EpisodeRepository episodeRepository;

    @Setter(onMethod_ = {@Autowired})
    private UserRecordRepository userRecordRepository;

    /**
     * @throws DataIntegrityException if can't extract a title
     */
    private static String extractTitle(List<SeasonEntity> seasons) throws DataIntegrityException {
        seasons.sort(Comparator.comparingInt(SeasonEntity::getCurrentSeason));
        String title = seasons.get(0).getZhTitle();
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
            Pattern pattern = Pattern
                .compile(encoded + "(" + currentSeason + "[一-龿]*| " + ji + ")");
            if (!pattern.matcher(season.getZhTitle()).matches()) {
                throw new DataIntegrityException("Can't extract title of the series.");
            }
        }
        return title;
    }

    @Override
    public SingleResult<Long> importSubjectByDb(long dbId)
        throws HttpResponseException, DataIntegrityException, NotFoundException {
        Optional<IdView<Long>> optionalMovie = movieRepository.findByDbId(dbId);
        if (optionalMovie.isPresent()) {
            return SingleResult.of(optionalMovie.get().getId());
        }
        Optional<IdView<Long>> optionalSeason = seasonRepository.findByDbId(dbId);
        if (optionalSeason.isPresent()) {
            return SingleResult.of(optionalSeason.get().getId());
        }

        log.info("Import a subject of Douban: {}", dbId);
        BaseDoubanSubject subject = adapter.doubanSubject(dbId);
        String imdbId = subject.getImdbId();
        if (subject instanceof DoubanMovie) {
            return insertMovie(Pair.of(dbId, (DoubanMovie) subject), Pair.of(imdbId, null));
        }

        if (imdbId == null) {
            if (subject.getYear() > Year.now().getValue()) {
                throw new DataIntegrityException("The subject isn't released yet.");
            }
            throw new DataIntegrityException("Can't save series without IMDb id.");
        }

        return insertSeries(imdbId, adapter.imdbView(imdbId));
    }

    @Override
    public SingleResult<Long> importSubjectByImdb(String imdbId)
        throws HttpResponseException, DataIntegrityException, NotFoundException {
        ImdbView imdbView = adapter.imdbView(imdbId);
        if (imdbView instanceof ImdbMovieView) {
            Optional<IdView<Long>> optional = movieRepository.findByImdbId(imdbId);
            if (optional.isPresent()) {
                return SingleResult.of(optional.get().getId());
            }
            Pair<String, ImdbMovieView> imdbResult = Pair.of(imdbId, (ImdbMovieView) imdbView);
            try {
                long dbId = adapter.getDbIdByImdbId(imdbId);
                return insertMovie(Pair.of(dbId, null), imdbResult);
            } catch (NotFoundException e) {
                return insertMovie(ImmutablePair.nullPair(), imdbResult);
            }
        }

        return insertSeries(imdbId, imdbView);
    }

    /**
     * Make sure at least one valid movie is provided.
     *
     * @throws DataIntegrityException if some required properties are lacking
     * @throws HttpResponseException  if an error occurs
     */
    private SingleResult<Long> insertMovie(Pair<Long, DoubanMovie> dbResult,
        Pair<String, ImdbMovieView> imdbResult)
        throws DataIntegrityException, HttpResponseException {
        Long dbId = dbResult.getLeft();
        DoubanMovie doubanMovie = dbResult.getRight();
        if (dbId != null && doubanMovie == null) {
            try {
                doubanMovie = (DoubanMovie) adapter.doubanSubject(dbId);
            } catch (NotFoundException e) {
                throw new ShouldExistException(e);
            }
        }
        String imdbId = imdbResult.getLeft();
        ImdbMovieView imdbMovieView = imdbResult.getRight();
        if (StringUtils.isNotBlank(imdbId) && imdbMovieView == null) {
            try {
                imdbMovieView = (ImdbMovieView) adapter.imdbView(imdbId);
            } catch (NotFoundException e) {
                throw new ShouldExistException(e);
            }
        }
        if (doubanMovie == null && imdbMovieView == null) {
            throw new IllegalArgumentException("At least one valid movie should be provided.");
        }

        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setDbId(dbId);
        movieEntity.setImdbId(imdbId);
        Set<Duration> durations = new HashSet<>();
        if (doubanMovie != null) {
            movieEntity.setZhTitle(doubanMovie.getZhTitle());
            movieEntity.setOriginalTitle(doubanMovie.getOriginalTitle());
            movieEntity.setYear(doubanMovie.getYear());
            movieEntity.setLanguages(doubanMovie.getLanguages());
            CollectionUtils.addIgnoreNull(durations, doubanMovie.getDuration());
            List<Runtime> runtimes = doubanMovie.getRuntimes();
            if (runtimes != null) {
                runtimes.stream().map(Runtime::getDuration).forEach(durations::add);
            }
        }
        if (imdbMovieView != null) {
            movieEntity.setEnTitle(imdbMovieView.getEnTitle());
            if (movieEntity.getYear() == null) {
                movieEntity.setYear(imdbMovieView.getYear());
            }
            if (movieEntity.getLanguages() == null) {
                movieEntity.setLanguages(imdbMovieView.getLanguages());
            }
            if (imdbMovieView.getDurations() != null) {
                durations.addAll(imdbMovieView.getDurations());
            }
        }
        if (!durations.isEmpty()) {
            movieEntity.setDurations(durations.stream().sorted().collect(Collectors.toList()));
        }
        try {
            return SingleResult.of(movieRepository.insert(movieEntity).getId());
        } catch (DataIntegrityViolationException e) {
            if (movieEntity.getYear() != null && movieEntity.getYear() > Year.now().getValue()) {
                throw new DataIntegrityException(MOVIE_NOT_RELEASED_MSSG);
            }
            if (doubanMovie != null && !doubanMovie.isReleased()) {
                throw new DataIntegrityException(MOVIE_NOT_RELEASED_MSSG);
            }
            throw new DataIntegrityException("Data of the movie isn't integral: " + e.getMessage());
        }
    }

    /**
     * Id and title are both required not null.
     *
     * @throws DataIntegrityException if some required properties are lacking
     * @throws HttpResponseException  if an error occurs when getting seasons
     */
    private SingleResult<Long> insertSeries(@Nonnull String imdbId, @Nonnull ImdbView imdbView)
        throws DataIntegrityException, HttpResponseException {
        String seriesImdbId;
        ImdbSeriesView imdbSeriesView;
        if (imdbView instanceof ImdbEpisodeView) {
            seriesImdbId = ((ImdbEpisodeView) imdbView).getSeriesId();
            try {
                imdbSeriesView = (ImdbSeriesView) adapter.imdbView(seriesImdbId);
            } catch (NotFoundException e) {
                throw new ShouldExistException(e);
            }
        } else if (imdbView instanceof ImdbSeriesView) {
            seriesImdbId = imdbId;
            imdbSeriesView = (ImdbSeriesView) imdbView;
        } else if (imdbView instanceof ImdbMovieView) {
            throw new DataIntegrityException(
                String.format("Conflict type: %s or %s expected but %s provided.",
                    ImdbSeriesView.class, ImdbEpisodeView.class, ImdbMovieView.class));
        } else {
            throw new UnknownTypeException(imdbView.getClass());
        }

        Pair<List<Pair<SeasonEntity, List<EpisodeEntity>>>, Map<Integer, String>> result =
            getSeasons(imdbSeriesView.getEpisodes(), seriesImdbId);
        List<Pair<SeasonEntity, List<EpisodeEntity>>> seasons = result.getLeft();
        Map<Integer, String> fails = result.getRight();
        SeriesEntity series;
        try {
            Optional<SeriesEntity> optional = seriesRepository.findByImdbId(seriesImdbId);
            if (optional.isEmpty()) {
                SeriesEntity seriesEntity = new SeriesEntity();
                seriesEntity.setImdbId(seriesImdbId);
                seriesEntity.setEnTitle(imdbSeriesView.getEnTitle());
                seriesEntity.setYear(imdbSeriesView.getYear());
                seriesEntity.setLanguages(imdbSeriesView.getLanguages());
                seriesEntity.setSeasonsCount(imdbSeriesView.getSeasonsCount());
                seriesEntity.setZhTitle(
                    extractTitle(seasons.stream().map(Pair::getLeft).collect(Collectors.toList())));
                series = seriesRepository.insert(seriesEntity);
            } else {
                series = optional.get();
            }
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException(
                "Data of the series aren't integral: " + e.getMessage());
        }

        for (Pair<SeasonEntity, List<EpisodeEntity>> pair : seasons) {
            SeasonEntity season = pair.getLeft();
            Optional<IdView<Long>> idViewOptional = seasonRepository.findByDbId(season.getDbId());
            try {
                long seasonId;
                if (idViewOptional.isEmpty()) {
                    season.setSeries(series);
                    seasonId = seasonRepository.insert(season).getId();
                } else {
                    seasonId = idViewOptional.get().getId();
                }
                for (EpisodeEntity episode : pair.getRight()) {
                    Optional<EpisodeEntity> entityOptional = episodeRepository
                        .findByImdbId(episode.getImdbId());
                    if (entityOptional.isEmpty()) {
                        episode.setSeasonId(seasonId);
                        episodeRepository.insert(episode);
                    }
                }
            } catch (DataIntegrityViolationException | IllegalArgumentException e) {
                if (season.getYear() != null && season.getYear() > Year.now().getValue()) {
                    fails.put(season.getCurrentSeason(), "The season isn't released yet.");
                } else {
                    fails.put(season.getCurrentSeason(), e.getMessage());
                }
            }
        }

        if (!fails.isEmpty()) {
            throw new DataIntegrityException(fails.entrySet().stream()
                .map(entry -> String
                    .format("Season: %d, error: %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(Constants.LINE_SEPARATOR)));
        }
        return SingleResult.of(series.getId());
    }

    private Pair<List<Pair<SeasonEntity, List<EpisodeEntity>>>, Map<Integer, String>>
    getSeasons(List<String[]> allEpisodes, String seriesImdbId) throws HttpResponseException {
        Map<Integer, String> fails = new HashMap<>(4);
        List<Pair<SeasonEntity, List<EpisodeEntity>>> seasons = new ArrayList<>();
        String[] season1Episodes = allEpisodes.isEmpty() ? new String[]{} : allEpisodes.get(0);
        try {
            seasons.add(getSeason(seriesImdbId, season1Episodes, 1));
        } catch (NotFoundException e) {
            fails.put(1, e.getMessage());
        }

        if (allEpisodes.size() > 1) {
            for (int index = 1; index < allEpisodes.size(); index++) {
                String[] episodes = allEpisodes.get(index);
                int currentSeason = index + 1;
                if (episodes == null || episodes[1] == null) {
                    fails.put(currentSeason, "None id of IMDb exists.");
                } else {
                    try {
                        seasons.add(getSeason(episodes[1], episodes, currentSeason));
                    } catch (NotFoundException e) {
                        fails.put(currentSeason, e.getMessage());
                    }
                }
            }
        }
        return Pair.of(seasons, fails);
    }

    /**
     * All args are required not null.
     *
     * @throws HttpResponseException if an error occurs
     * @throws NotFoundException     if the subject of the given IMDb id is not found from Douban
     */
    private Pair<SeasonEntity, List<EpisodeEntity>> getSeason(String seasonImdbId,
        String[] episodes,
        int currentSeason)
        throws HttpResponseException, NotFoundException {
        Long seasonDbId = adapter.getDbIdByImdbId(seasonImdbId);
        BaseDoubanSubject subject = adapter.doubanSubject(seasonDbId);

        SeasonEntity seasonEntity = new SeasonEntity();
        seasonEntity.setDbId(seasonDbId);
        seasonEntity.setZhTitle(subject.getZhTitle());
        seasonEntity.setOriginalTitle(subject.getOriginalTitle());
        seasonEntity.setYear(subject.getYear());
        seasonEntity.setLanguages(subject.getLanguages());
        Set<Duration> durations = new HashSet<>();
        List<Runtime> runtimes = subject.getRuntimes();
        if (runtimes != null) {
            runtimes.stream().map(Runtime::getDuration).forEach(durations::add);
        }
        CollectionUtils.addIgnoreNull(durations, subject.getDuration());
        if (!durations.isEmpty()) {
            seasonEntity.setDurations(durations.stream().sorted().collect(Collectors.toList()));
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
        return Pair.of(seasonEntity, episodeEntities);
    }

    private EpisodeEntity getEpisode(String episodeImdbId, int currentEpisode)
        throws HttpResponseException {
        EpisodeEntity episodeEntity = new EpisodeEntity();
        episodeEntity.setImdbId(episodeImdbId);

        ImdbEpisodeView imdbEpisodeView;
        try {
            imdbEpisodeView = (ImdbEpisodeView) adapter.imdbView(episodeImdbId);
        } catch (NotFoundException e) {
            throw new ShouldExistException(e);
        }
        episodeEntity.setEnTitle(imdbEpisodeView.getEnTitle());
        episodeEntity.setDurations(imdbEpisodeView.getDurations());
        episodeEntity.setCurrentEpisode(currentEpisode);
        return episodeEntity;
    }

    @Override
    public BatchResult<Long> importDouban(long userId, @Nullable LocalDate since, DoubanMark mark)
        throws HttpResponseException, NotFoundException {
        if (since == null) {
            since = userRecordRepository.findMaxMarkDate(userId)
                .orElse(DoubanSite.DOUBAN_START_DATE);
        }
        log.info("Start to import douban subjects marked as {} of {} since {}", mark, userId,
            since);
        Map<Long, LocalDate> map = adapter.collectUserSubjects(userId, since, mark);
        int count = 0;
        Map<Long, String> fails = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (Map.Entry<Long, LocalDate> entry : map.entrySet()) {
            Long id;
            try {
                id = importSubjectByDb(entry.getKey()).getRecord();
            } catch (DataIntegrityException | NotFoundException e) {
                fails.put(entry.getKey(), e.getMessage());
                continue;
            }
            count++;
            UserRecordEntity entity = new UserRecordEntity();
            entity.setMark(mark);
            entity.setMarkDate(entry.getValue());
            entity.setSubjectId(id);
            entity.setUserId(userId);
            userRecordRepository.updateOrInsert(entity,
                () -> userRecordRepository.findBySubjectIdAndUserId(id, userId));
        }
        return new BatchResult<>(count, fails);
    }

    @Override
    public List<MovieEntity> listMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Optional<MovieEntity> getMovie(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public Map<SeriesEntity, List<SeasonEntity>> listSeries() {
        List<SeasonEntity> seasons = seasonRepository.findAll();
        return seasons.stream().collect(Collectors.groupingBy(SeasonEntity::getSeries));
    }

    @Override
    public Pair<SeriesEntity, List<SeasonEntity>> getSeries(Long id) {
        List<SeasonEntity> seasons = seasonRepository.findAllBySeriesId(id);
        if (seasons.isEmpty()) {
            Optional<SeriesEntity> optional = seriesRepository.findById(id);
            if (optional.isEmpty()) {
                return ImmutablePair.nullPair();
            } else {
                return Pair.of(optional.get(), seasons);
            }
        }
        return Pair.of(seasons.get(0).getSeries(), seasons);
    }

    @Override
    public Optional<SeasonEntity> getSeason(Long id) {
        return seasonRepository.findById(id);
    }
}
