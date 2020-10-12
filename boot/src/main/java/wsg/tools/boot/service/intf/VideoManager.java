package wsg.tools.boot.service.intf;

import wsg.tools.boot.pojo.entity.*;
import wsg.tools.boot.pojo.enums.ArchivedStatus;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;

/**
 * Management of local videos.
 *
 * @author Kingen
 * @since 2020/10/11
 */
public interface VideoManager {

    /**
     * Obtains corresponding file of the given movie.
     * <p>
     * {@link #archive(MovieEntity)} should be called to check the status of the given movie.
     * The file is available when status is {@link ArchivedStatus#ARCHIVED}.
     *
     * @param movie given movie
     * @return optional file of the movie
     */
    Optional<File> getFile(MovieEntity movie);

    /**
     * Obtains corresponding directory of the given tv series.
     *
     * @param series given tv series
     * @return optional directory of the series
     */
    Optional<File> getFile(SeriesEntity series);

    /**
     * Obtains corresponding directory of the given season of tv series.
     *
     * @param season given tv season
     * @return optional directory of the season
     */
    Optional<File> getFile(SeasonEntity season);

    /**
     * Obtains corresponding directory of the given episode of tv series.
     *
     * @param episode given tv episode
     * @return optional file of the episode
     */
    Optional<File> getFile(EpisodeEntity episode);

    /**
     * Archive the given movie.
     *
     * @param movie given movie
     * @return status of archiving
     */
    ArchivedStatus archive(MovieEntity movie);

    /**
     * Archive the given season.
     *
     * @param season given season
     * @return status of archiving
     */
    ArchivedStatus archive(SeasonEntity season);

    /**
     * Archive the given tv series.
     *
     * @param series    given tv series
     * @param semaphore a semaphore between seasons
     * @return map of season-status
     */
    Map<Integer, ArchivedStatus> archive(SeriesEntity series, Semaphore semaphore);

    /**
     * Obtains corresponding location of the given entity
     *
     * @param entity given entity
     * @return location of the given entity
     */
    String getLocation(SubjectEntity entity);
}