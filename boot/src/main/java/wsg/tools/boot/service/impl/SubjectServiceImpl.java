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
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.common.Runtime;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.douban.BaseDoubanSubject;
import wsg.tools.internet.video.site.douban.DoubanMovie;
import wsg.tools.internet.video.site.douban.DoubanSeries;
import wsg.tools.internet.video.site.douban.DoubanSite;
import wsg.tools.internet.video.site.imdb.ImdbEpisode;
import wsg.tools.internet.video.site.imdb.ImdbMovie;
import wsg.tools.internet.video.site.imdb.ImdbSeries;
import wsg.tools.internet.video.site.imdb.ImdbTitle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
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
    public SingleResult<Long> importSubjectByDb(long dbId) throws NotFoundException, SiteException, DataIntegrityException {
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
            return insertMovie(BiResult.of(dbId, (DoubanMovie) subject), BiResult.of(imdbId, null));
        }

        if (imdbId == null) {
            if (subject.getYear() > Year.now().getValue()) {
                throw new DataIntegrityException("The subject isn't released yet.");
            }
            throw new DataIntegrityException("Can't save series without IMDb id.");
        }

        ImdbTitle imdbTitle = adapter.imdbTitle(imdbId).getRecord();
        return insertSeries(imdbId, imdbTitle);
    }

    @Override
    public SingleResult<Long> importSubjectByImdb(String imdbId) throws NotFoundException, SiteException, DataIntegrityException {
        ImdbTitle imdbTitle = adapter.imdbTitle(imdbId).getRecord();
        if (imdbTitle instanceof ImdbMovie) {
            Optional<IdView<Long>> optional = movieRepository.findByImdbId(imdbId);
            if (optional.isPresent()) {
                return SingleResult.of(optional.get().getId());
            }
            BiResult<String, ImdbMovie> imdbResult = BiResult.of(imdbId, (ImdbMovie) imdbTitle);
            try {
                BiResult<Long, DoubanMovie> dbResult = BiResult.of(adapter.getDbIdByImdbId(imdbId).getRecord(), null);
                return insertMovie(dbResult, imdbResult);
            } catch (SiteException | NotFoundException ignored) {
                return insertMovie(BiResult.empty(), imdbResult);
            }
        }

        return insertSeries(imdbId, imdbTitle);
    }

    /**
     * Make sure at least one valid movie is provided.
     */
    private SingleResult<Long> insertMovie(BiResult<Long, DoubanMovie> dbResult, BiResult<String, ImdbMovie> imdbResult) throws DataIntegrityException {
        Long dbId = dbResult.getLeft();
        DoubanMovie doubanMovie = dbResult.getRight();
        if (dbId != null && doubanMovie == null) {
            try {
                doubanMovie = (DoubanMovie) adapter.doubanSubject(dbId).getRecord();
            } catch (NotFoundException e) {
                log.error(e.getMessage());
            }
        }
        String imdbId = imdbResult.getLeft();
        ImdbMovie imdbMovie = imdbResult.getRight();
        if (StringUtils.isNotBlank(imdbId) && imdbMovie == null) {
            try {
                imdbMovie = (ImdbMovie) adapter.imdbTitle(imdbId).getRecord();
            } catch (NotFoundException e) {
                log.error(e.getMessage());
            }
        }
        if (doubanMovie == null && imdbMovie == null) {
            throw new IllegalArgumentException("At least one valid movie should be provided.");
        }

        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setDbId(dbId);
        movieEntity.setImdbId(imdbId);
        Set<Duration> durations = new HashSet<>();
        if (doubanMovie != null) {
            movieEntity.setTitle(doubanMovie.getTitle());
            movieEntity.setOriginalTitle(doubanMovie.getOriginalTitle());
            movieEntity.setYear(doubanMovie.getYear());
            movieEntity.setLanguages(doubanMovie.getLanguages());
            CollectionUtils.addIgnoreNull(durations, doubanMovie.getDuration());
            List<Runtime> runtimes = doubanMovie.getRuntimes();
            if (runtimes != null) {
                runtimes.stream().map(Runtime::getDuration).forEach(durations::add);
            }
        }
        if (imdbMovie != null) {
            movieEntity.setText(imdbMovie.getText());
            if (movieEntity.getYear() == null) {
                movieEntity.setYear(imdbMovie.getYear());
            }
            if (movieEntity.getLanguages() == null) {
                movieEntity.setLanguages(imdbMovie.getLanguages());
            }
            if (imdbMovie.getRuntimes() != null) {
                durations.addAll(imdbMovie.getRuntimes());
            }
        }
        if (!durations.isEmpty()) {
            movieEntity.setDurations(durations.stream().sorted().collect(Collectors.toList()));
        }
        try {
            return SingleResult.of(movieRepository.insert(movieEntity).getId());
        } catch (DataIntegrityViolationException e) {
            if (movieEntity.getYear() != null && movieEntity.getYear() > Year.now().getValue()) {
                throw new DataIntegrityException("The movie isn't released yet.");
            }
            if (doubanMovie != null && !doubanMovie.isReleased()) {
                throw new DataIntegrityException("The movie isn't released yet.");
            }
            throw new DataIntegrityException("Data of the movie isn't integral: " + e.getMessage());
        }
    }

    /**
     * Id and title are both required not null.
     */
    private SingleResult<Long> insertSeries(@Nonnull String imdbId, @Nonnull ImdbTitle title)
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

        List<String[]> allEpisodes = imdbSeries.getEpisodes();
        int seasonsCount = allEpisodes.size();
        if (seasonsCount == 0) {
            seasonsCount = 1;
        }

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
                final int currentSeason = index + 1;
                if (episodes == null || episodes[1] == null) {
                    fails.put(currentSeason, "None id of IMDb exists.");
                } else {
                    try {
                        seasons.add(getSeason(episodes[1], episodes, currentSeason));
                    } catch (NotFoundException | SiteException e) {
                        fails.put(currentSeason, e.getMessage());
                    }
                }
            }
        }

        SeriesEntity series;
        try {
            Optional<SeriesEntity> optional = seriesRepository.findByImdbId(seriesImdbId);
            if (optional.isEmpty()) {
                SeriesEntity seriesEntity = new SeriesEntity();
                seriesEntity.setImdbId(seriesImdbId);
                seriesEntity.setText(imdbSeries.getText());
                seriesEntity.setYear(imdbSeries.getRangeYear().getStart());
                if (imdbSeries.getLanguages() != null) {
                    seriesEntity.setLanguages(imdbSeries.getLanguages());
                }
                seriesEntity.setSeasonsCount(seasonsCount);
                seriesEntity.setTitle(extractTitle(seasons.stream().map(BiResult::getLeft).collect(Collectors.toList())));
                series = seriesRepository.insert(seriesEntity);
            } else {
                series = optional.get();
            }
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Data of the series aren't integral: " + e.getMessage());
        }

        for (BiResult<SeasonEntity, List<EpisodeEntity>> result : seasons) {
            SeasonEntity season = result.getLeft();
            Optional<IdView<Long>> idViewOptional = seasonRepository.findByDbId(season.getDbId());
            try {
                long seasonId;
                if (idViewOptional.isEmpty()) {
                    season.setSeries(series);
                    seasonId = seasonRepository.insert(season).getId();
                } else {
                    seasonId = idViewOptional.get().getId();
                }
                for (EpisodeEntity episode : result.getRight()) {
                    Optional<EpisodeEntity> entityOptional = episodeRepository.findByImdbId(episode.getImdbId());
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
                    .map(entry -> String.format("Season: %d, error: %s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(Constants.LINE_SEPARATOR)));
        }
        return SingleResult.of(series.getId());
    }

    private String extractTitle(List<SeasonEntity> seasons) throws DataIntegrityException {
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
            if (!pattern.matcher(season.getTitle()).matches()) {
                throw new DataIntegrityException("Can't extract title of the series.");
            }
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
        return BiResult.of(seasonEntity, episodeEntities);
    }

    private EpisodeEntity getEpisode(String episodeImdbId, int currentEpisode) {
        EpisodeEntity episodeEntity = new EpisodeEntity();
        episodeEntity.setImdbId(episodeImdbId);

        ImdbTitle imdbTitle;
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
    public BatchResult<Long> importDouban(long userId, @Nullable LocalDate since, MarkEnum mark) {
        if (since == null) {
            since = userRecordRepository.findMaxMarkDate().orElse(DoubanSite.DOUBAN_START_DATE);
        }
        log.info("Start to import douban subjects marked as {} of {} since {}", mark, userId, since);
        Map<Long, LocalDate> map;
        try {
            map = adapter.collectUserSubjects(userId, since, mark).getRecord();
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return new BatchResult<>(0, Collections.emptyMap());
        }
        int count = 0;
        Map<Long, String> fails = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (Map.Entry<Long, LocalDate> entry : map.entrySet()) {
            Long id;
            try {
                id = this.importSubjectByDb(entry.getKey()).getRecord();
            } catch (SiteException | DataIntegrityException | NotFoundException e) {
                fails.put(entry.getKey(), e.getMessage());
                continue;
            }
            count++;
            UserRecordEntity entity = new UserRecordEntity();
            entity.setMark(mark);
            entity.setMarkDate(entry.getValue());
            entity.setSubjectId(id);
            entity.setUserId(userId);
            userRecordRepository.updateOrInsert(entity, () -> userRecordRepository.findBySubjectIdAndUserId(id, userId));
        }
        return new BatchResult<>(count, fails);
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
    public Map<SeriesEntity, List<SeasonEntity>> listSeries() {
        List<SeasonEntity> seasons = seasonRepository.findAll();
        return seasons.stream().collect(Collectors.groupingBy(SeasonEntity::getSeries));
    }

    @Override
    public BiResult<SeriesEntity, List<SeasonEntity>> getSeries(Long id) {
        List<SeasonEntity> seasons = seasonRepository.findAllBySeriesId(id);
        if (seasons.isEmpty()) {
            Optional<SeriesEntity> optional = seriesRepository.findById(id);
            if (optional.isEmpty()) {
                return BiResult.of(null, null);
            } else {
                return BiResult.of(optional.get(), seasons);
            }
        }
        return BiResult.of(seasons.get(0).getSeries(), seasons);
    }

    @Override
    public Optional<SeasonEntity> getSeason(Long id) {
        return seasonRepository.findById(id);
    }
}
