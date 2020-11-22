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
     * @param cdn   cdn
     * @param movie given movie
     * @return optional file of the movie
     */
    Optional<File> getFile(File cdn, MovieEntity movie);

    /**
     * Obtains corresponding directory of the given tv series.
     *
     * @param cdn    cdn
     * @param series given tv series
     * @return optional directory of the series
     */
    Optional<File> getFile(File cdn, SeriesEntity series);

    /**
     * Obtains corresponding directory of the given season of tv series.
     *
     * @param cdn    cdn
     * @param season given tv season
     * @return optional directory of the season
     */
    Optional<File> getFile(File cdn, SeasonEntity season);

    /**
     * Obtains corresponding directory of the given episode of tv series.
     *
     * @param cdn     cdn
     * @param episode given tv episode
     * @return optional file of the episode
     */
    Optional<File> getFile(File cdn, EpisodeEntity episode);

    /**
     * Archive the given movie.
     * <p>
     * Firstly, locate the file from cdn. Otherwise, find under temporary directory.
     * If still not found, search download by {@link ResourceService#search(String, Long, String)}.
     *
     * @param cdn    cdn
     * @param tmpdir temporary directory to store downloading files
     * @param movie  given movie
     * @return status of archiving
     */
    VideoStatus archive(File cdn, File tmpdir, MovieEntity movie);

    /**
     * Archive the given season from the cdn.
     * <p>
     * Firstly, locate the file from cdn. Otherwise, find under temporary directory.
     * If still not found, search and download by {@link ResourceService#search(String, Long, String)}.
     *
     * @param cdn    cdn
     * @param tmpdir temporary directory to store downloading files
     * @param season given season
     * @return status of archiving
     */
    VideoStatus archive(File cdn, File tmpdir, SeasonEntity season);
}