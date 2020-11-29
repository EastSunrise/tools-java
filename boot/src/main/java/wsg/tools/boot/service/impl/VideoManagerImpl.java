package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.enums.VideoArchivedStatus;
import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.boot.config.PathConfiguration;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.boot.service.intf.VideoManager;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.io.Filetype;
import wsg.tools.internet.resource.download.Thunder;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Management of local videos.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Slf4j
@Service
public class VideoManagerImpl extends BaseServiceImpl implements VideoManager {

    public static final Pattern EPISODE_FILE_REGEX = Pattern.compile("[^\\d]*(?<e>\\d+)[^\\d]*");

    private final ResourceService resourceService;
    private final PathConfiguration pathConfig;

    public VideoManagerImpl(ResourceService resourceService, PathConfiguration pathConfig) {
        this.resourceService = resourceService;
        this.pathConfig = pathConfig;
    }

    @Override
    public final Optional<File> getFile(MovieEntity movie) {
        String location = pathConfig.getLocation(movie);
        for (Filetype type : Filetype.videoTypes()) {
            File file = new File(location + SignEnum.FILE_EXTENSION_SEPARATOR + type.suffix());
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
    public final VideoStatus archive(MovieEntity movie, boolean chosen) throws IOException {
        Optional<File> optional = getFile(movie);
        if (optional.isPresent()) {
            return VideoArchivedStatus.archived(optional.get());
        }

        File tempDir = pathConfig.tmpdir(movie);
        if (tempDir.isDirectory()) {
            if (!FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.tmpTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
                return VideoStatus.DOWNLOADING;
            }
            Collection<File> videoFiles = FileUtils.listFiles(tempDir, Filetype.fileFilter(Filetype.videoTypes()), TrueFileFilter.INSTANCE);
            if (videoFiles.isEmpty()) {
                return VideoStatus.NONE_DOWNLOADED;
            }
            if (!chosen || videoFiles.size() > 1) {
                return VideoStatus.TO_ARCHIVE;
            }

            File srcFile = videoFiles.iterator().next();
            String suffix = Filetype.getRealType(srcFile).suffix();
            File destFile = new File(pathConfig.getLocation(movie) + SignEnum.FILE_EXTENSION_SEPARATOR + suffix);
            FileUtils.moveFile(srcFile, destFile);
            FileUtils.deleteDirectory(tempDir);
            return VideoArchivedStatus.archived(destFile);
        }

        return download(tempDir, movie.getDbId(), movie.getImdbId());
    }

    @Override
    public VideoStatus archive(SeasonEntity season, boolean chosen) throws IOException {
        Optional<File> optional = getFile(season);
        if (optional.isPresent()) {
            return VideoArchivedStatus.archived(optional.get());
        }

        File tempDir = pathConfig.tmpdir(season);
        if (tempDir.isDirectory()) {
            if (!FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.tmpTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
                return VideoStatus.DOWNLOADING;
            }
            Collection<File> videoFiles = FileUtils.listFiles(tempDir, Filetype.fileFilter(Filetype.videoTypes()), TrueFileFilter.INSTANCE);
            if (videoFiles.isEmpty()) {
                return VideoStatus.NONE_DOWNLOADED;
            }
            if (!chosen || videoFiles.size() != season.getEpisodesCount()) {
                return VideoStatus.TO_ARCHIVE;
            }

            Map<String, File> movedFiles = new HashMap<>(season.getEpisodesCount());
            for (File srcFile : videoFiles) {
                Matcher matcher = EPISODE_FILE_REGEX.matcher(FilenameUtils.getBaseName(srcFile.getName()));
                if (!matcher.matches()) {
                    return VideoStatus.TO_ARCHIVE;
                }
                int currentEpisode = Integer.parseInt(matcher.group("e"));
                if (currentEpisode < 1 || currentEpisode > season.getEpisodesCount()) {
                    return VideoStatus.TO_ARCHIVE;
                }
                String location = pathConfig.getLocation(season, currentEpisode);
                if (movedFiles.containsKey(location)) {
                    return VideoStatus.TO_ARCHIVE;
                }
                movedFiles.put(location, srcFile);
            }
            for (Map.Entry<String, File> entry : movedFiles.entrySet()) {
                File srcFile = entry.getValue();
                log.info("Moving {} as {}.", srcFile, entry.getKey());
                String suffix = Filetype.getRealType(srcFile).suffix();
                File destFile = new File(entry.getKey() + SignEnum.FILE_EXTENSION_SEPARATOR + suffix);
                FileUtils.moveFile(srcFile, destFile);
            }
            log.info("Deleting {}.", tempDir);
            FileUtils.deleteDirectory(tempDir);
            return VideoArchivedStatus.archived(new File(pathConfig.getLocation(season)));
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
