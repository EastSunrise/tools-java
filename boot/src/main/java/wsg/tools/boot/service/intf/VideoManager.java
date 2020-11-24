package wsg.tools.boot.service.intf;

import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.boot.pojo.entity.subject.EpisodeEntity;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;

import java.io.File;
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
     * Obtains corresponding directory of the given episode of tv series.
     *
     * @param episode given tv episode
     * @return optional file of the episode
     */
    Optional<File> getFile(EpisodeEntity episode);

    /**
     * Archive the given movie.
     * <p>
     * Firstly, locate the file from cdn. Otherwise, find under temporary directory.
     * If still not found, search download by {@link ResourceService#search(String, Long, String)}.
     *
     * @param movie given movie
     * @return status of archiving
     */
    VideoStatus archive(MovieEntity movie);

    /**
     * Archive the given season from the cdn.
     * <p>
     * Firstly, locate the file from cdn. Otherwise, find under temporary directory.
     * If still not found, search and download by {@link ResourceService#search(String, Long, String)}.
     *
     * @param season given season
     * @return status of archiving
     */
    VideoStatus archive(SeasonEntity season);
}