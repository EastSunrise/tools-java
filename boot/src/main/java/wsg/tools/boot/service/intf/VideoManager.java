package wsg.tools.boot.service.intf;

import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Management of local video.
 *
 * @author Kingen
 * @since 2020/10/11
 */
public interface VideoManager {

    /**
     * Obtains corresponding file of the given movie.
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
     * Obtains archiving status of the given movie.
     * <p>
     * Firstly, locate the file from cdn. Otherwise, find under temporary directory.
     *
     * @param movie given movie
     * @return status of archiving
     */
    VideoStatus getStatus(MovieEntity movie);

    /**
     * Obtains archiving status of the given season.
     * <p>
     * Firstly, locate the file from cdn. Otherwise, find under temporary directory.
     *
     * @param season given season
     * @return status of archiving
     */
    VideoStatus getStatus(SeasonEntity season);

    /**
     * Archives the given movie.
     *
     * @param movie given movie
     * @return result of archiving
     * @throws IOException if an error occurs when edit files.
     */
    VideoStatus archive(MovieEntity movie) throws IOException;

    /**
     * Archives the given season.
     *
     * @param season given season
     * @return result of archiving
     * @throws IOException if an error occurs when edit files.
     */
    VideoStatus archive(SeasonEntity season) throws IOException;
}