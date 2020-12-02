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
import wsg.tools.boot.pojo.entity.subject.SubjectEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.VideoManager;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.util.function.throwable.ThrowableBiFunction;
import wsg.tools.internet.resource.download.Thunder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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

    private final PathConfiguration pathConfig;

    public VideoManagerImpl(PathConfiguration pathConfig) {
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
        return archive(movie, this::getFile, 1, chosen, (videoFiles, tempDir) -> {
            File srcFile = videoFiles.iterator().next();
            String suffix = Filetype.getRealType(srcFile).suffix();
            File destFile = new File(pathConfig.getLocation(movie) + SignEnum.FILE_EXTENSION_SEPARATOR + suffix);
            FileUtils.moveFile(srcFile, destFile);
            FileUtils.deleteDirectory(tempDir);
            return VideoArchivedStatus.archived(destFile);
        });
    }

    @Override
    public VideoStatus archive(SeasonEntity season, boolean chosen) throws IOException {
        return archive(season, this::getFile, season.getEpisodesCount(), chosen, (videoFiles, tempDir) -> {
            Map<String, File> movedFiles = new HashMap<>(season.getEpisodesCount());
            for (File srcFile : videoFiles) {
                Matcher matcher = EPISODE_FILE_REGEX.matcher(FilenameUtils.getBaseName(srcFile.getName()));
                if (!matcher.matches()) {
                    return VideoStatus.TO_CHOOSE;
                }
                int currentEpisode = Integer.parseInt(matcher.group("e"));
                if (currentEpisode < 1 || currentEpisode > season.getEpisodesCount()) {
                    return VideoStatus.TO_CHOOSE;
                }
                String location = pathConfig.getLocation(season, currentEpisode);
                if (movedFiles.containsKey(location)) {
                    return VideoStatus.TO_CHOOSE;
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
        });
    }

    private <E extends SubjectEntity> VideoStatus archive(
            E entity, Function<E, Optional<File>> getFile, int count, boolean chosen,
            ThrowableBiFunction<Collection<File>, File, VideoStatus, IOException> move) throws IOException {
        Optional<File> optional = getFile.apply(entity);
        if (optional.isPresent()) {
            return VideoArchivedStatus.archived(optional.get());
        }

        File tempDir = pathConfig.tmpdir(entity);
        if (!tempDir.isDirectory()) {
            if (!tempDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + tempDir);
            }
            return VideoStatus.TO_DOWNLOAD;
        }

        if (!FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.tmpTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
            return VideoStatus.DOWNLOADING;
        }
        Collection<File> videoFiles = FileUtils.listFiles(tempDir, Filetype.fileFilter(Filetype.videoTypes()), TrueFileFilter.INSTANCE);
        if (videoFiles.size() < count) {
            return VideoStatus.LACKING;
        }
        if (videoFiles.size() > count || !chosen) {
            return VideoStatus.TO_CHOOSE;
        }

        return move.apply(videoFiles, tempDir);
    }
}
