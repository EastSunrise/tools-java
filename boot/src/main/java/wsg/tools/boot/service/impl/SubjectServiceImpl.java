package wsg.tools.boot.service.impl;

import java.time.Duration;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import wsg.tools.boot.dao.api.ImdbEpisodeView;
import wsg.tools.boot.dao.api.ImdbMovieView;
import wsg.tools.boot.dao.api.ImdbSeriesView;
import wsg.tools.boot.dao.api.ImdbView;
import wsg.tools.boot.dao.api.support.SiteManager;
import wsg.tools.boot.dao.jpa.mapper.EpisodeRepository;
import wsg.tools.boot.dao.jpa.mapper.MovieRepository;
import wsg.tools.boot.dao.jpa.mapper.SeasonRepository;
import wsg.tools.boot.dao.jpa.mapper.SeriesRepository;
import wsg.tools.boot.pojo.entity.base.IdView;
import wsg.tools.boot.pojo.entity.subject.EpisodeEntity;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.error.DataIntegrityException;
import wsg.tools.boot.pojo.error.ShouldExistException;
import wsg.tools.boot.pojo.error.UnknownTypeException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.base.page.FixedSizePageReq;
import wsg.tools.internet.common.LoginException;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.movie.common.Runtime;
import wsg.tools.internet.movie.common.enums.DoubanMark;
import wsg.tools.internet.movie.douban.AbstractMovie;
import wsg.tools.internet.movie.douban.DoubanCatalog;
import wsg.tools.internet.movie.douban.DoubanMovie;
import wsg.tools.internet.movie.douban.DoubanPageResult;
import wsg.tools.internet.movie.douban.DoubanSeries;
import wsg.tools.internet.movie.douban.MarkedSubject;

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

    private MovieRepository movieRepository;
    private SeriesRepository seriesRepository;
    private SeasonRepository seasonRepository;
    private EpisodeRepository episodeRepository;
    private SiteManager manager;

    @Override
    public long importSubjectByDb(long dbId)
        throws NotFoundException, OtherResponseException, DataIntegrityException {
        Optional<IdView<Long>> idOp = movieRepository.findByDbId(dbId);
        if (idOp.isPresent()) {
            return idOp.get().getId();
        }
        idOp = seasonRepository.findByDbId(dbId);
        if (idOp.isPresent()) {
            return idOp.get().getId();
        }

        log.info("Import a subject of Douban: {}", dbId);
        AbstractMovie movie = manager.doubanRepo().findMovieById(dbId);
        String imdbId = movie.getImdbId();
        if (movie instanceof DoubanMovie) {
            return insertMovie(Pair.of(dbId, (DoubanMovie) movie), Pair.of(imdbId, null));
        }

        if (imdbId == null) {
            if (movie.getYear() > Year.now().getValue()) {
                throw new DataIntegrityException("The subject isn't released yet.");
            }
            throw new DataIntegrityException("Can't save series without IMDb id.");
        }

        return insertNotMovie(imdbId, manager.imdbRepo().findSubjectById(imdbId));
    }

    @Override
    public long importSubjectByImdb(String imdbId)
        throws DataIntegrityException, NotFoundException, OtherResponseException {
        ImdbView imdbView = manager.imdbRepo().findSubjectById(imdbId);
        if (imdbView instanceof ImdbMovieView) {
            Optional<IdView<Long>> optional = movieRepository.findByImdbId(imdbId);
            if (optional.isPresent()) {
                return optional.get().getId();
            }
            Pair<String, ImdbMovieView> imdbResult = Pair.of(imdbId, (ImdbMovieView) imdbView);
            try {
                long dbId = manager.doubanRepo().getDbIdByImdbId(imdbId);
                return insertMovie(Pair.of(dbId, null), imdbResult);
            } catch (NotFoundException | LoginException e) {
                return insertMovie(ImmutablePair.nullPair(), imdbResult);
            }
        }

        return insertNotMovie(imdbId, imdbView);
    }

    /**
     * Make sure at least one valid movie is provided. The id, if provided, should be valid.
     *
     * @throws DataIntegrityException if some required properties are lacking
     */
    private long insertMovie(Pair<Long, DoubanMovie> dbResult,
        Pair<String, ImdbMovieView> imdbResult)
        throws DataIntegrityException, OtherResponseException {
        Long dbId = dbResult.getLeft();
        DoubanMovie doubanMovie = dbResult.getRight();
        if (dbId != null && doubanMovie == null) {
            try {
                doubanMovie = (DoubanMovie) manager.doubanRepo().findMovieById(dbId);
            } catch (NotFoundException e) {
                throw new ShouldExistException(e);
            }
        }
        String imdbId = imdbResult.getLeft();
        ImdbMovieView imdbMovieView = imdbResult.getRight();
        if (StringUtils.isNotBlank(imdbId) && imdbMovieView == null) {
            try {
                imdbMovieView = (ImdbMovieView) manager.imdbRepo().findSubjectById(imdbId);
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
            return movieRepository.insert(movieEntity).getId();
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

    private long insertNotMovie(@Nonnull String imdbId, @Nonnull ImdbView imdbView)
        throws OtherResponseException, DataIntegrityException {
        if (imdbView instanceof ImdbMovieView) {
            String message = String.format("Conflict type: %s or %s expected but %s provided.",
                ImdbSeriesView.class, ImdbEpisodeView.class, ImdbMovieView.class);
            throw new DataIntegrityException(message);
        }
        String seriesImdbId;
        ImdbSeriesView imdbSeriesView;
        if (imdbView instanceof ImdbEpisodeView) {
            seriesImdbId = ((ImdbEpisodeView) imdbView).getSeriesId();
            try {
                imdbSeriesView = (ImdbSeriesView) manager.imdbRepo().findSubjectById(seriesImdbId);
            } catch (NotFoundException e) {
                throw new ShouldExistException(e);
            }
        } else if (imdbView instanceof ImdbSeriesView) {
            seriesImdbId = imdbId;
            imdbSeriesView = (ImdbSeriesView) imdbView;
        } else {
            throw new UnknownTypeException(imdbView.getClass());
        }
        return insertSeries(seriesImdbId, imdbSeriesView);
    }

    /**
     * Id and view are both required not null.
     */
    private long insertSeries(String seriesImdbId, ImdbSeriesView seriesView)
        throws OtherResponseException, DataIntegrityException {
        Pair<List<Pair<SeasonEntity, List<EpisodeEntity>>>, Map<Integer, String>> result =
            getSeasons(seriesView.getEpisodes(), seriesImdbId);
        List<Pair<SeasonEntity, List<EpisodeEntity>>> seasons = result.getLeft();
        Map<Integer, String> fails = result.getRight();
        SeriesEntity series;
        try {
            Optional<SeriesEntity> optional = seriesRepository.findByImdbId(seriesImdbId);
            if (optional.isEmpty()) {
                SeriesEntity seriesEntity = new SeriesEntity();
                seriesEntity.setImdbId(seriesImdbId);
                seriesEntity.setEnTitle(seriesView.getEnTitle());
                seriesEntity.setYear(seriesView.getYear());
                seriesEntity.setLanguages(seriesView.getLanguages());
                seriesEntity.setSeasonsCount(seriesView.getSeasonsCount());
                List<SeasonEntity> seasonEntities = seasons.stream().map(Pair::getLeft)
                    .collect(Collectors.toList());
                seriesEntity.setZhTitle(extractTitle(seasonEntities));
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
        return series.getId();
    }

    /**
     * @throws DataIntegrityException if can't extract a title
     */
    private String extractTitle(List<SeasonEntity> seasons) throws DataIntegrityException {
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

    private Pair<List<Pair<SeasonEntity, List<EpisodeEntity>>>, Map<Integer, String>>
    getSeasons(List<String[]> allEpisodes, String seriesImdbId) throws OtherResponseException {
        Map<Integer, String> fails = new HashMap<>(4);
        List<Pair<SeasonEntity, List<EpisodeEntity>>> seasons = new ArrayList<>();
        String[] season1Episodes = allEpisodes.isEmpty() ? new String[]{} : allEpisodes.get(0);
        try {
            seasons.add(getSeason(seriesImdbId, season1Episodes, 1));
        } catch (LoginException | NotFoundException e) {
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
                    } catch (LoginException | NotFoundException e) {
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
     * @throws NotFoundException if the subject of the given IMDb id is not found from Douban
     */
    private Pair<SeasonEntity, List<EpisodeEntity>> getSeason(String seasonImdbId,
        String[] episodes, int currentSeason)
        throws OtherResponseException, NotFoundException, LoginException {
        long seasonDbId = manager.doubanRepo().getDbIdByImdbId(seasonImdbId);
        AbstractMovie movie = null;
        try {
            movie = manager.doubanRepo().findMovieById(seasonDbId);
        } catch (NotFoundException e) {
            throw new ShouldExistException(e);
        }

        SeasonEntity seasonEntity = new SeasonEntity();
        seasonEntity.setDbId(seasonDbId);
        seasonEntity.setZhTitle(movie.getZhTitle());
        seasonEntity.setOriginalTitle(movie.getOriginalTitle());
        seasonEntity.setYear(movie.getYear());
        seasonEntity.setLanguages(movie.getLanguages());
        Set<Duration> durations = new HashSet<>();
        List<Runtime> runtimes = movie.getRuntimes();
        if (runtimes != null) {
            runtimes.stream().map(Runtime::getDuration).forEach(durations::add);
        }
        CollectionUtils.addIgnoreNull(durations, movie.getDuration());
        if (!durations.isEmpty()) {
            seasonEntity.setDurations(durations.stream().sorted().collect(Collectors.toList()));
        }
        seasonEntity.setEpisodesCount(((DoubanSeries) movie).getEpisodesCount());
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

    /**
     * Retrieves and converts an episode.
     */
    private EpisodeEntity getEpisode(String episodeImdbId, int currentEpisode)
        throws OtherResponseException {
        EpisodeEntity episodeEntity = new EpisodeEntity();
        episodeEntity.setImdbId(episodeImdbId);

        ImdbEpisodeView imdbEpisodeView;
        try {
            imdbEpisodeView = (ImdbEpisodeView) manager.imdbRepo().findSubjectById(episodeImdbId);
        } catch (NotFoundException e) {
            throw new ShouldExistException(e);
        }
        episodeEntity.setEnTitle(imdbEpisodeView.getEnTitle());
        episodeEntity.setDurations(imdbEpisodeView.getDurations());
        episodeEntity.setCurrentEpisode(currentEpisode);
        return episodeEntity;
    }

    @Override
    public BatchResult<Long> importDouban(long userId, @Nonnull DoubanMark mark)
        throws NotFoundException, OtherResponseException {
        log.info("Start to import collected subjects marked as {} of {}", mark, userId);
        FixedSizePageReq req = FixedSizePageReq.first();
        BatchResult<Long> result = BatchResult.create();
        while (true) {
            DoubanPageResult<MarkedSubject> subjects = manager.doubanRepo()
                .findUserSubjects(DoubanCatalog.MOVIE, userId, mark, req);
            for (MarkedSubject subject : subjects.getContent()) {
                try {
                    importSubjectByDb(subject.getDbId());
                } catch (DataIntegrityException | NotFoundException e) {
                    result.fail(subject.getDbId(), e.getMessage());
                    continue;
                }
                result.succeed();
            }
            if (!subjects.hasNext()) {
                break;
            }
            req = subjects.nextPageRequest();
        }
        return result;
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
    public void setManager(SiteManager manager) {
        this.manager = manager;
    }
}
