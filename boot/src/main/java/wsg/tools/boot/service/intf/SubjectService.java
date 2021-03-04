package wsg.tools.boot.service.intf;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.error.DataIntegrityException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.pojo.result.ListResult;
import wsg.tools.boot.pojo.result.SingleResult;
import wsg.tools.internet.movie.common.enums.MarkEnum;

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
     * @throws HttpResponseException  if an error occurs
     * @throws DataIntegrityException if some required properties are lacking
     * @throws NotFoundException      if the subject of the given id is not found from Douban
     */
    SingleResult<Long> importSubjectByDb(long dbId) throws HttpResponseException, DataIntegrityException, NotFoundException;

    /**
     * Import a subject obtained by id of IMDb.
     *
     * @param imdbId id of IMDb, not null
     * @return result with subject id
     * @throws HttpResponseException  if an error occurs
     * @throws DataIntegrityException if some required properties are lacking
     * @throws NotFoundException      if the subject of the given id is not found from IMDb
     */
    SingleResult<Long> importSubjectByImdb(String imdbId) throws HttpResponseException, DataIntegrityException, NotFoundException;

    /**
     * Import subjects from Douban of the given user.
     *
     * @param userId user id
     * @param since  since when
     * @param mark   marking type
     * @return result of importing
     * @throws HttpResponseException if an error occurs
     * @throws NotFoundException     if subjects of the given user are not found from Douban
     */
    BatchResult<Long> importDouban(long userId, @Nullable LocalDate since, MarkEnum mark) throws HttpResponseException, NotFoundException;

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
     * @return pair of series-seasons
     */
    Pair<SeriesEntity, List<SeasonEntity>> getSeries(Long id);

    /**
     * Obtains the series of the given id
     *
     * @param id id of series to get
     * @return result of series
     */
    Optional<SeasonEntity> getSeason(Long id);
}
