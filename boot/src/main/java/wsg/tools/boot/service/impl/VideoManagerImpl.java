package wsg.tools.boot.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.Year;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.Functions;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.boot.config.PathConfiguration;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.VideoManager;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.download.impl.Thunder;
import wsg.tools.internet.resource.common.YearSupplier;

/**
 * Management of local videos.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Slf4j
@Service
public class VideoManagerImpl extends BaseServiceImpl implements VideoManager {

    private static final Pattern EPISODE_FILE_REGEX =
        Pattern.compile("[^\\d]*((?<year>\\d{4})[^\\d]+)?(S(?<s>\\d{1,2})E)?(?<e>\\d{1,3})([^\\d]+"
            + "(AC3|X264|1080P|720P|1280x720|1024X576))*[^\\d]*", Pattern.CASE_INSENSITIVE);

    private static final String[] VIDEO_SUFFIXES = {"mp4", "mkv"};

    private static final SuffixFileFilter VIDEO_FILTER = new SuffixFileFilter(VIDEO_SUFFIXES,
        IOCase.INSENSITIVE);

    private final PathConfiguration pathConfig;

    public VideoManagerImpl(PathConfiguration pathConfig) {
        this.pathConfig = pathConfig;
    }

    @Override
    public Optional<File> getFile(MovieEntity movie) {
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
    public Optional<File> getFile(SeriesEntity series) {
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
        return getStatus(movie, this::getFile, 1, movie.getZhTitle());
    }

    @Override
    public VideoStatus getStatus(SeasonEntity season) {
        return getStatus(season, this::getFile, season.getEpisodesCount(), season.getZhTitle());
    }

    private <T extends YearSupplier> VideoStatus getStatus(T t, Function<T, Optional<File>> getFile,
        int count,
        String zhTitle) {
        try {
            return archive(t, getFile, count, zhTitle, null);
        } catch (IOException e) {
            throw new AppException(e);
        }
    }

    @Override
    public VideoStatus archive(MovieEntity movie) throws IOException {
        return archive(movie, this::getFile, 1, movie.getZhTitle(), (tempDir, videoFiles) -> {
            File srcFile = videoFiles.iterator().next();
            String suffix = FilenameUtils.getExtension(srcFile.getName());
            File destFile = new File(
                pathConfig.getLocation(movie) + Constants.FILE_EXTENSION_SEPARATOR + suffix);
            FileUtils.moveFile(srcFile, destFile);
            FileUtils.deleteDirectory(tempDir);
            return VideoStatus.ARCHIVED;
        });
    }

    @Override
    public VideoStatus archive(SeasonEntity season) throws IOException {
        return archive(season, this::getFile, season.getEpisodesCount(), season.getZhTitle(),
            (tempDir, videoFiles) -> {
                Map<String, File> movedFiles = new HashMap<>(season.getEpisodesCount());
                for (File srcFile : videoFiles) {
                    Matcher matcher = EPISODE_FILE_REGEX
                        .matcher(FilenameUtils.getBaseName(srcFile.getName()));
                    if (!matcher.matches()) {
                        log.info("Not matched file: {}", srcFile);
                        return VideoStatus.TO_CHOOSE;
                    }
                    String year = matcher.group("year");
                    if (year != null && Integer.parseInt(year) != season.getYear()) {
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
                    File destFile = new File(
                        entry.getKey() + Constants.FILE_EXTENSION_SEPARATOR + suffix);
                    FileUtils.moveFile(srcFile, destFile);
                }
                FileUtils.deleteDirectory(tempDir);
                return VideoStatus.ARCHIVED;
            });
    }

    private <T extends YearSupplier> VideoStatus archive(T t, Function<T, Optional<File>> getFile,
        int count,
        String zhTitle,
        @Nullable Functions.FailableBiFunction<File, Collection<File>, VideoStatus, IOException> ifToArchive)
        throws IOException {
        if (Year.now().getValue() < t.getYear()) {
            return VideoStatus.COMING;
        }
        Optional<File> optional = getFile.apply(t);
        if (optional.isPresent()) {
            return VideoStatus.ARCHIVED;
        }
        File tempDir = pathConfig.tmpdir(zhTitle);
        if (!tempDir.isDirectory()) {
            return VideoStatus.TO_DOWNLOAD;
        }
        if (!FileUtils.listFiles(tempDir, Thunder.tmpFileFilter(), TrueFileFilter.INSTANCE)
            .isEmpty()) {
            return VideoStatus.DOWNLOADING;
        }
        Collection<File> videoFiles = FileUtils
            .listFiles(tempDir, VIDEO_FILTER, TrueFileFilter.INSTANCE);
        if (videoFiles.size() < count) {
            return VideoStatus.LACKING;
        }
        if (videoFiles.size() > count) {
            return VideoStatus.TO_CHOOSE;
        }
        if (ifToArchive == null) {
            return VideoStatus.TO_ARCHIVE;
        }
        return ifToArchive.apply(tempDir, videoFiles);
    }
}
