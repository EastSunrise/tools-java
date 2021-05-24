package wsg.tools.boot.service.intf;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.error.DataIntegrityException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.movie.common.enums.DoubanMark;

/**
 * Interface of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
public interface SubjectService {

    /**
     * Imports a subject of the specified dbId.
     *
     * @param dbId the dbId of the subject to be imported
     * @return the generated id of the saved subject
     * @throws OtherResponseException if an unexpected error occurs
     * @throws DataIntegrityException if some required properties are lacking
     * @throws NotFoundException      if the subject of the given id is not found from Douban
     */
    long importSubjectByDb(long dbId)
        throws DataIntegrityException, NotFoundException, OtherResponseException;

    /**
     * Imports a subject of the specified imdbId.
     *
     * @param imdbId id of IMDb, not blank
     * @return the generated id of the saved subject
     * @throws OtherResponseException   if an unexpected error occurs
     * @throws DataIntegrityException   if some required properties are lacking
     * @throws NotFoundException        if the subject of the given id is not found from IMDb
     * @throws IllegalArgumentException if the specified imdbId is blank
     */
    long importSubjectByImdb(String imdbId)
        throws OtherResponseException, DataIntegrityException, NotFoundException;

    /**
     * Imports collected subjects the specified user.
     *
     * @param userId user id
     * @param mark   marking type
     * @return result of importing
     * @throws OtherResponseException if an unexpected error occurs
     * @throws NotFoundException      if subjects of the specified user are not found
     */
    BatchResult<Long> importDouban(long userId, @Nonnull DoubanMark mark)
        throws OtherResponseException, NotFoundException;

    /**
     * Obtains all subjects of movies.
     *
     * @return list of all movies
     */
    List<MovieEntity> listMovies();

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
