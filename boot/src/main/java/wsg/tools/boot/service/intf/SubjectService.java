package wsg.tools.boot.service.intf;

import wsg.tools.boot.pojo.base.DataIntegrityException;
import wsg.tools.boot.pojo.base.ListResult;
import wsg.tools.boot.pojo.base.SingleResult;
import wsg.tools.boot.pojo.base.SiteException;
import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.entity.SeasonEntity;
import wsg.tools.boot.pojo.entity.SeriesEntity;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.internet.base.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
public interface SubjectService {

    /**
     * Insert entity obtained by id of douban.
     *
     * @param dbId id of Douban
     * @return result with subject id
     * @throws NotFoundException      if not found
     * @throws SiteException          if an error occurs when accessing to the site
     * @throws DataIntegrityException if data is lacking
     */
    SingleResult<Long> insertSubjectByDb(long dbId) throws NotFoundException, SiteException, DataIntegrityException;

    /**
     * Insert entity obtained by id of IMDb.
     *
     * @param imdbId id of IMDb, not null
     * @return result with subject id
     * @throws NotFoundException      if not found
     * @throws SiteException          if an error occurs when accessing to the site
     * @throws DataIntegrityException if data is lacking
     */
    SingleResult<Long> insertSubjectByImdb(String imdbId) throws NotFoundException, SiteException, DataIntegrityException;

    /**
     * Import subjects from Douban of the given user.
     *
     * @param userId user id
     * @param since  since when
     * @return result of importing
     */
    BatchResult<Long> importDouban(long userId, LocalDate since);

    /**
     * Obtains all subjects of movies.
     *
     * @return list result of all movies
     */
    ListResult<MovieEntity> listMovies();

    /**
     * Obtains the movie of the given id
     *
     * @param id id of movie to get
     * @return result of movie
     */
    Optional<MovieEntity> getMovie(Long id);

    /**
     * Obtains all subjects of series.
     *
     * @return list result of all series
     */
    ListResult<SeriesEntity> listSeries();

    /**
     * Obtains the series of the given id
     *
     * @param id id of series to get
     * @return result of series
     */
    Optional<SeriesEntity> getSeries(Long id);

    /**
     * Obtains the season of the given series
     *
     * @param seriesId seriesId of series
     * @return result of seasons
     */
    List<SeasonEntity> getSeasonsBySeries(Long seriesId);
}
