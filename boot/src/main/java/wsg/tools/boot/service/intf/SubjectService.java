package wsg.tools.boot.service.intf;

import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.error.DataIntegrityException;
import wsg.tools.boot.pojo.error.SiteException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.pojo.result.BiResult;
import wsg.tools.boot.pojo.result.ListResult;
import wsg.tools.boot.pojo.result.SingleResult;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.enums.MarkEnum;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
public interface SubjectService {

    /**
     * Import a subject obtained by id of douban.
     *
     * @param dbId id of Douban
     * @return result with subject id
     * @throws NotFoundException      if not found
     * @throws SiteException          if an error occurs when accessing to the site
     * @throws DataIntegrityException if data is lacking
     */
    SingleResult<Long> importSubjectByDb(long dbId) throws NotFoundException, SiteException, DataIntegrityException;

    /**
     * Import a subject obtained by id of IMDb.
     *
     * @param imdbId id of IMDb, not null
     * @return result with subject id
     * @throws NotFoundException      if not found
     * @throws SiteException          if an error occurs when accessing to the site
     * @throws DataIntegrityException if data is lacking
     */
    SingleResult<Long> importSubjectByImdb(String imdbId) throws NotFoundException, SiteException, DataIntegrityException;

    /**
     * Import subjects from Douban of the given user.
     *
     * @param userId user id
     * @param since  since when
     * @param mark   marking type
     * @return result of importing
     */
    BatchResult<Long> importDouban(long userId, @Nullable LocalDate since, MarkEnum mark);

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
     * @return list result of all series with seasons
     */
    Map<SeriesEntity, List<SeasonEntity>> listSeries();

    /**
     * Obtains the series of the given id
     *
     * @param id id of series to get
     * @return result of series
     */
    BiResult<SeriesEntity, List<SeasonEntity>> getSeries(Long id);

    /**
     * Obtains the series of the given id
     *
     * @param id id of series to get
     * @return result of series
     */
    Optional<SeasonEntity> getSeason(Long id);
}
