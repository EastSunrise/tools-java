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
     * Archive the given movie.
     * <p>
     * Firstly, locate the file from cdn. Otherwise, find under temporary directory.
     * If still not found, search download by {@link ResourceService#search(String, Long, String)}.
     *
     * @param movie  given movie
     * @param chosen if downloaded files have been chosen
     * @return status of archiving
     * @throws IOException if an error occurs when edit files.
     */
    VideoStatus archive(MovieEntity movie, boolean chosen) throws IOException;

    /**
     * Archive the given season from the cdn.
     * <p>
     * Firstly, locate the file from cdn. Otherwise, find under temporary directory.
     * If still not found, search and download by {@link ResourceService#search(String, Long, String)}.
     *
     * @param season given season
     * @param chosen if downloaded files have been chosen
     * @return status of archiving
     * @throws IOException if an error occurs when edit files.
     */
    VideoStatus archive(SeasonEntity season, boolean chosen) throws IOException;
}