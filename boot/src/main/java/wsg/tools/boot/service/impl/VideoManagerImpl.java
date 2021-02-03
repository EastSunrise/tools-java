package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.boot.config.PathConfiguration;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.VideoManager;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.resource.download.Thunder;

import java.io.File;
import java.io.IOException;
import java.time.Year;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Management of local videos.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Slf4j
@Service
public class VideoManagerImpl extends BaseServiceImpl implements VideoManager {

    public static final Pattern EPISODE_FILE_REGEX =
            Pattern.compile("[^\\d]*((?<year>\\d{4})[^\\d]+)?(S(?<s>\\d{1,2})E)?(?<e>\\d{1,3})([^\\d]+" +
                    "(AC3|X264|1080P|720P|1280x720|1024X576))*[^\\d]*", Pattern.CASE_INSENSITIVE);
    private static final String[] VIDEO_SUFFIXES = new String[]{"mp4", "mkv"};
    private static final SuffixFileFilter VIDEO_FILTER = new SuffixFileFilter(VIDEO_SUFFIXES, IOCase.INSENSITIVE);

    private final PathConfiguration pathConfig;

    public VideoManagerImpl(PathConfiguration pathConfig) {
        this.pathConfig = pathConfig;
    }

    @Override
    public final Optional<File> getFile(MovieEntity movie) {
        String location = pathConfig.getLocation(movie);
        for (String suffix : VIDEO_SUFFIXES) {
            File file = new File(location + Constants.FILE_EXTENSION_SEPARATOR + suffix);
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
    public VideoStatus getStatus(MovieEntity movie) {
        return getStatus(movie.getYear(), () -> this.getFile(movie), movie.getTitle(), 1);
    }

    @Override
    public VideoStatus getStatus(SeasonEntity season) {
        return getStatus(season.getYear(), () -> this.getFile(season), season.getTitle(), season.getEpisodesCount());
    }

    private VideoStatus getStatus(Year year, Supplier<Optional<File>> getFile, String title, int count) {
        if (Year.now().compareTo(year) < 0) {
            return VideoStatus.COMING;
        }
        Optional<File> optional = getFile.get();
        if (optional.isPresent()) {
            return VideoStatus.ARCHIVED;
        }
        File tempDir = pathConfig.tmpdir(title);
        if (!tempDir.isDirectory()) {
            return VideoStatus.TO_DOWNLOAD;
        }
        if (!FileUtils.listFiles(tempDir, Thunder.tmpFileFilter(), TrueFileFilter.INSTANCE).isEmpty()) {
            return VideoStatus.DOWNLOADING;
        }
        Collection<File> videoFiles = FileUtils.listFiles(tempDir, VIDEO_FILTER, TrueFileFilter.INSTANCE);
        if (videoFiles.size() < count) {
            return VideoStatus.LACKING;
        }
        if (videoFiles.size() > count) {
            return VideoStatus.TO_CHOOSE;
        }
        return VideoStatus.TO_ARCHIVE;

    }

    @Override
    public VideoStatus archive(MovieEntity movie) throws IOException {
        VideoStatus status = getStatus(movie);
        if (status != VideoStatus.TO_ARCHIVE) {
            return status;
        }

        File tempDir = pathConfig.tmpdir(movie.getTitle());
        Collection<File> videoFiles = FileUtils.listFiles(tempDir, VIDEO_FILTER, TrueFileFilter.INSTANCE);
        File srcFile = videoFiles.iterator().next();
        String suffix = FilenameUtils.getExtension(srcFile.getName());
        File destFile = new File(pathConfig.getLocation(movie) + Constants.FILE_EXTENSION_SEPARATOR + suffix);
        FileUtils.moveFile(srcFile, destFile);
        FileUtils.deleteDirectory(tempDir);
        return VideoStatus.ARCHIVED;
    }

    @Override
    public VideoStatus archive(SeasonEntity season) throws IOException {
        VideoStatus status = getStatus(season);
        if (status != VideoStatus.TO_ARCHIVE) {
            return status;
        }

        File tempDir = pathConfig.tmpdir(season.getTitle());
        Map<String, File> movedFiles = new HashMap<>(season.getEpisodesCount());
        Collection<File> videoFiles = FileUtils.listFiles(tempDir, VIDEO_FILTER, TrueFileFilter.INSTANCE);
        for (File srcFile : videoFiles) {
            Matcher matcher = EPISODE_FILE_REGEX.matcher(FilenameUtils.getBaseName(srcFile.getName()));
            if (!matcher.matches()) {
                log.info("Not matched file: {}", srcFile);
                return VideoStatus.TO_CHOOSE;
            }
            String year = matcher.group("year");
            if (year != null && Integer.parseInt(year) != season.getYear().getValue()) {
                log.info("Not matched year: {}", srcFile);
                return VideoStatus.TO_CHOOSE;
            }
            String s = matcher.group("s");
            if (s != null && Integer.parseInt(s) != season.getCurrentSeason()) {
                log.info("Not matched season: {}", srcFile);
                return VideoStatus.TO_CHOOSE;
            }
            int currentEpisode = Integer.parseInt(matcher.group("e"));
            if (currentEpisode < 1 || currentEpisode > season.getEpisodesCount()) {
                log.info("Not matched episode: {}", srcFile);
                return VideoStatus.TO_CHOOSE;
            }
            String location = pathConfig.getLocation(season, currentEpisode);
            if (movedFiles.containsKey(location)) {
                log.info("Duplicate episodes: {}", srcFile);
                return VideoStatus.TO_CHOOSE;
            }
            movedFiles.put(location, srcFile);
        }
        List<Map.Entry<String, File>> entries = movedFiles.entrySet().stream()
                .sorted(Map.Entry.<String, File>comparingByKey().reversed())
                .collect(Collectors.toList());
        for (Map.Entry<String, File> entry : entries) {
            File srcFile = entry.getValue();
            log.info("Moving {} as {}.", srcFile, entry.getKey());
            String suffix = FilenameUtils.getExtension(srcFile.getName());
            File destFile = new File(entry.getKey() + Constants.FILE_EXTENSION_SEPARATOR + suffix);
            FileUtils.moveFile(srcFile, destFile);
        }
        FileUtils.deleteDirectory(tempDir);
        return VideoStatus.ARCHIVED;

    }
}
