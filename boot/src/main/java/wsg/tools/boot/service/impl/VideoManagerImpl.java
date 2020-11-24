package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.enums.VideoArchivedStatus;
import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.boot.config.PathConfiguration;
import wsg.tools.boot.dao.jpa.mapper.EpisodeRepository;
import wsg.tools.boot.dao.jpa.mapper.SeasonRepository;
import wsg.tools.boot.pojo.entity.subject.EpisodeEntity;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.boot.service.intf.VideoManager;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.resource.download.Thunder;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Management of local videos.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Slf4j
@Service
public class VideoManagerImpl extends BaseServiceImpl implements VideoManager {

    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;
    private final ResourceService resourceService;
    private final PathConfiguration pathConfig;

    public VideoManagerImpl(
            SeasonRepository seasonRepository, EpisodeRepository episodeRepository, ResourceService resourceService, PathConfiguration pathConfig) {
        this.seasonRepository = seasonRepository;
        this.episodeRepository = episodeRepository;
        this.resourceService = resourceService;
        this.pathConfig = pathConfig;
    }

    @Override
    public final Optional<File> getFile(MovieEntity movie) {
        String location = pathConfig.getLocation(movie);
        for (Filetype type : Filetype.videoTypes()) {
            File file = new File(location + SignEnum.DOT + type.suffix());
            if (file.isFile()) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    @Override
    public final Optional<File> getFile(SeriesEntity series) {
        String location = pathConfig.getLocation(series);
        File seriesDir = new File(location);
        if (seriesDir.isDirectory()) {
            return Optional.of(seriesDir);
        }
        return Optional.empty();
    }

    @Override
    public Optional<File> getFile(SeasonEntity season) {
        String location = pathConfig.getLocation(season);
        File seasonDir = new File(location);
        if (seasonDir.isDirectory()) {
            return Optional.of(seasonDir);
        }
        return Optional.empty();
    }

    @Override
    public Optional<File> getFile(EpisodeEntity episode) {
        SeasonEntity season = seasonRepository.findById(episode.getSeasonId()).orElseThrow();
        Optional<File> optional = getFile(season);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        String format = "E%0" + (((int) Math.log10(season.getEpisodesCount())) + 1) + "d";
        String location = optional.get() + File.separator + String.format(format, episode.getCurrentEpisode());
        for (Filetype type : Filetype.videoTypes()) {
            File file = new File(location + SignEnum.DOT + type.suffix());
            if (file.isFile()) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    @Override
    public final VideoStatus archive(MovieEntity movie) {
        Optional<File> optional = getFile(movie);
        if (optional.isPresent()) {
            return VideoArchivedStatus.archived(optional.get());
        }

        File tempDir = pathConfig.tmpdir(movie);
        if (tempDir.isDirectory()) {
            if (Objects.requireNonNull(tempDir.list()).length == 0) {
                return VideoStatus.NONE_DOWNLOADED;
            }
            if (!FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.tmpTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
                return VideoStatus.DOWNLOADING;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("Target: ").append(pathConfig.getLocation(movie)).append(Constants.LINE_SEPARATOR);
            builder.append("Durations: ").append(StringUtils.join(movie.getDurations(), "/"));
            try {
                FileUtils.write(new File(tempDir, "info.txt"), builder.toString());
            } catch (IOException e) {
                throw AssertUtils.runtimeException(e);
            }
            return VideoStatus.TO_ARCHIVE;
        }

        return download(tempDir, movie.getDbId(), movie.getImdbId());
    }

    @Override
    public VideoStatus archive(SeasonEntity season) {
        SeriesEntity series = season.getSeries();
        File seriesDir = new File(pathConfig.getLocation(series));
        File seasonDir = series.getSeasonsCount() == 1 ? seriesDir
                : new File(seriesDir, String.format("S%02d", season.getCurrentSeason()));
        if (seasonDir.isDirectory()) {
            return VideoArchivedStatus.archived(seasonDir);
        }

        File tempDir = pathConfig.tmpdir(season);
        if (tempDir.isDirectory()) {
            if (Objects.requireNonNull(tempDir.list()).length == 0) {
                return VideoStatus.NONE_DOWNLOADED;
            }
            if (!FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.tmpTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
                return VideoStatus.DOWNLOADING;
            }

            List<String> lines = new LinkedList<>();
            lines.add("Target: " + pathConfig.getLocation(season));
            lines.add("Season durations: " + StringUtils.join(season.getDurations(), "/"));
            lines.add("Episodes count: " + season.getEpisodesCount());
            String format = "Ep%0" + (((int) Math.log10(season.getEpisodesCount())) + 1) + "d";
            for (EpisodeEntity episode : episodeRepository.findAllBySeasonId(season.getId())) {
                if (episode != null && episode.getDurations() != null) {
                    lines.add(String.format(format, episode.getCurrentEpisode()) + ": " +
                            StringUtils.join(episode.getDurations(), "/"));
                }
            }
            try {
                FileUtils.writeLines(new File(tempDir, "info.txt"), lines);
            } catch (IOException e) {
                throw AssertUtils.runtimeException(e);
            }
            return VideoStatus.TO_ARCHIVE;
        }

        return download(tempDir, season.getDbId(), null);
    }

    private VideoStatus download(File tmpdir, @Nullable Long dbId, @Nullable String imdbId) {
        Long count = resourceService.download(tmpdir, dbId, imdbId).getRecord();
        if (count == -1) {
            return VideoStatus.NONE_FOUND;
        }
        if (count == 0) {
            return VideoStatus.NONE_DOWNLOADED;
        }
        return VideoStatus.TO_DOWNLOAD;
    }
}
