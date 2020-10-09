package wsg.tools.boot.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import wsg.tools.boot.dao.api.VideoAdapter;
import wsg.tools.boot.dao.jpa.mapper.MovieRepository;
import wsg.tools.boot.dao.jpa.mapper.SeriesRepository;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.entity.SeasonEntity;
import wsg.tools.boot.pojo.entity.SeriesEntity;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.StringUtilsExt;
import wsg.tools.internet.resource.download.Downloader;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.resource.*;
import wsg.tools.internet.resource.site.BaseResourceSite;
import wsg.tools.internet.video.enums.LanguageEnum;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * Management of local videos.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Slf4j
@Service
@PropertySource("classpath:config/private/video.properties")
public class VideoManager extends BaseServiceImpl {

    private static final SignEnum NAME_SEPARATOR = SignEnum.UNDERSCORE;
    private static final String TV_DIR = "TV";
    private static final String MOVIE_DIR = "Movies";
    private static final Filetype[] GOOD_VIDEO_SUFFIXES = new Filetype[]{Filetype.MP4, Filetype.MKV};

    /**
     * standard bps of files of a movie in KB/s.
     */
    private static final int MOVIE_STANDARD_KBPS = 250;
    /**
     * Permit size of a file mustn't be half smaller nor four bigger than the standard duration.
     */
    private static final double MOVIE_SIZE_FLOOR = 0.5;
    private static final int MOVIE_SIZE_CEIL = 4;
    /**
     * Permit duration of a file mustn't be 30s shorter nor 90s longer than the standard duration.
     */
    private static final int MOVIE_DURATION_FLOOR = -180;
    private static final int MOVIE_DURATION_CEIL = 180;

    static {
        System.setProperty("java.awt.headless", "false");
    }

    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final VideoAdapter adapter;
    private final ThreadPoolTaskExecutor executor;
    private final Downloader downloader = new Thunder();
    @Value("${video.tmpdir}")
    private String tmpdir;
    @Value("${video.cdn}")
    private String cdn;

    public VideoManager(MovieRepository movieRepository, SeriesRepository seriesRepository,
                        VideoAdapter adapter, ThreadPoolTaskExecutor executor) {
        this.movieRepository = movieRepository;
        this.seriesRepository = seriesRepository;
        this.adapter = adapter;
        this.executor = executor;
    }

    public BatchResult<String> collect() {
        Semaphore semaphore = new Semaphore(0);
        Semaphore closed = new Semaphore(0);
        executor.submit(() -> {
            JFrame frame = new JFrame("Next");
            frame.setSize(100, 62);
            JButton button = new JButton("Next");
            frame.getContentPane().add(button);
            frame.setVisible(true);
            button.addActionListener(e -> semaphore.release());
            try {
                closed.acquire();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            frame.dispose();
        });

        int count = 0;
        Map<String, String> fails = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (MovieEntity entity : movieRepository.findAll()) {
            ArchivedStatus status = this.archive(entity);
            if (status == ArchivedStatus.ARCHIVED) {
                count++;
            } else {
                if (status == ArchivedStatus.ADDED) {
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        throw AssertUtils.runtimeException(e);
                    }
                }
                fails.put(entity.getTitle(), status.toString());
            }
        }
        for (SeriesEntity entity : seriesRepository.findAll()) {
            ArchivedStatus status = this.archive(entity);
            if (status == ArchivedStatus.ARCHIVED) {
                count++;
            } else {
                if (status == ArchivedStatus.ADDED) {
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        throw AssertUtils.runtimeException(e);
                    }
                }
                fails.put(entity.getTitle(), status.toString());
            }
        }
        closed.release();
        return new BatchResult<>(count, fails);
    }

    /**
     * Obtains status when archiving the given tv series.
     * <p>
     * Firstly, locate the file under {@link #cdn}. Otherwise, find under temporary directory.
     * If still not found, search by {@link BaseResourceSite}s and download by {@link #downloader}.
     */
    public final ArchivedStatus archive(SeriesEntity entity) {
        // todo archive tv series
        return ArchivedStatus.NONE_FOUND;
    }

    /**
     * Obtains corresponding directory of the given tv series.
     */
    public final Optional<File> getFile(SeriesEntity entity) {
        String location = getLocation(entity);
        File seriesDir = new File(location);
        if (seriesDir.isDirectory()) {
            List<SeasonEntity> seasons = entity.getSeasons();
            if (seasons.size() > 1) {
                for (SeasonEntity season : seasons) {
                    File seasonDir = new File(location + File.separator + String.format("S%02d", season.getCurrentSeason()));
                    if (!seasonDir.isDirectory()) {
                        log.error("Not found season: {}.", seasonDir);
                    } else if (FileUtils.listFiles(seasonDir, null, false).size() != season.getEpisodesCount()) {
                        log.error("Lack of episodes: {}.", seasonDir);
                    }
                }
            }
            return Optional.of(seriesDir);
        }

        return Optional.empty();
    }

    /**
     * Obtains status when archiving the given movie.
     * <p>
     * Firstly, locate the file under {@link #cdn}. Otherwise, find under temporary directory.
     * If still not found, search by {@link BaseResourceSite}s and download by {@link #downloader}.
     */
    public final ArchivedStatus archive(MovieEntity entity) {
        Optional<File> optional = getFile(entity);
        if (optional.isPresent()) {
            return ArchivedStatus.ARCHIVED;
        }

        String tmpName = entity.getId() + "" + SignEnum.UNDERSCORE + entity.getTitle();
        File tempDir = new File(String.join(File.separator, tmpdir, tmpName));
        if (tempDir.isDirectory()) {
            if (Objects.requireNonNull(tempDir.list()).length == 0) {
                return ArchivedStatus.NONE_DOWNLOADED;
            }
            if (FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.downloadingTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
                Map<File, GenericResult<Integer>> map = FileUtils.listFiles(tempDir, null, true).stream()
                        .collect(Collectors.toMap(file -> file, file -> this.weight(file, entity.getDurations())));
                Map.Entry<File, GenericResult<Integer>> max = map.entrySet().stream()
                        .max(Comparator.comparingInt(entry -> (entry.getValue().orElse(-1)))).orElseThrow();
                GenericResult<Integer> result = max.getValue();
                if (!result.isSuccess()) {
                    String message = map.entrySet().stream()
                            .map(entry -> String.format("Reason: %s File: %s", entry.getValue().error(), entry.getKey().getName()))
                            .collect(Collectors.joining("\n"));
                    ArchivedStatus status = ArchivedStatus.NO_QUALIFIED;
                    status.setMsg(message);
                    return status;
                }
                File srcFile = max.getKey();
                File destFile = new File(getLocation(entity) + srcFile.getName().substring(srcFile.getName().lastIndexOf(SignEnum.DOT.getC())));
                try {
                    FileUtils.moveFile(srcFile, destFile);
                    FileUtils.deleteDirectory(tempDir);
                } catch (IOException e) {
                    throw AssertUtils.runtimeException(e);
                }
                return ArchivedStatus.ARCHIVED;
            } else {
                return ArchivedStatus.DOWNLOADING;
            }
        }

        final long maxSize = entity.getDurations().stream()
                .max(Duration::compareTo).orElseThrow()
                .plusSeconds(MOVIE_DURATION_CEIL)
                .getSeconds() * MOVIE_SIZE_CEIL;
        final long minSize = (long) (entity.getDurations().stream()
                .min(Duration::compareTo).orElseThrow()
                .plusSeconds(MOVIE_DURATION_FLOOR)
                .getSeconds() * MOVIE_SIZE_FLOOR);
        Set<AbstractResource> resources = adapter.searchResources(entity);
        int count = resources.stream().filter(resource -> this.filter(resource, minSize, maxSize))
                .mapToInt(resource -> {
                    try {
                        downloader.download(tempDir, resource);
                        return 1;
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        return 0;
                    }
                }).sum();
        if (count == 0) {
            return ArchivedStatus.NONE_FOUND;
        }
        return ArchivedStatus.ADDED;
    }

    /**
     * Obtains corresponding file of the given movie.
     */
    public final Optional<File> getFile(MovieEntity entity) {
        String location = getLocation(entity);
        for (Filetype type : Filetype.videoTypes()) {
            File file = new File(location + SignEnum.DOT + type.suffix());
            if (file.isFile()) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    private boolean filter(AbstractResource resource, long minSize, long maxSize) {
        if (resource instanceof InvalidResource) {
            return false;
        }
        if (resource instanceof PanResource) {
            return false;
        }
        if (resource instanceof Ed2kResource || resource instanceof HttpResource) {
            Filetype filetype = Filetype.typeOf(resource.filename());
            if (filetype == null || !filetype.isVideo()) {
                return false;
            }
        }
        long size = resource.size();
        return size < 0 || size >= minSize && size <= maxSize;
    }

    private GenericResult<Integer> weight(@Nonnull File file, final List<Duration> durations) {
        int[] weights = new int[3];
        Filetype filetype = Filetype.typeOf(file.getName());
        if (filetype == null || !filetype.isVideo()) {
            return new GenericResult<>("Not a video file.");
        } else if (ArrayUtils.contains(GOOD_VIDEO_SUFFIXES, filetype)) {
            weights[0] = 100;
        } else {
            weights[0] = 50;
        }

        int size = durations.size();
        if (size < 1) {
            throw new IllegalArgumentException("Duration mustn't be null.");
        }
        durations.sort(Duration::compareTo);
        Duration fileDuration;
        try {
            long millis = new MultimediaObject(file).getInfo().getDuration();
            if (millis < 0) {
                return new GenericResult<>("Can't get duration.");
            }
            fileDuration = Duration.ofMillis(millis);
        } catch (EncoderException e) {
            return new GenericResult<>(e);
        }

        int[] i = new int[1];
        boolean anyMatch = durations.stream().anyMatch(duration -> {
            if (fileDuration.compareTo(duration.plusSeconds(MOVIE_DURATION_FLOOR)) > 0 &&
                    fileDuration.compareTo(duration.plusSeconds(MOVIE_DURATION_CEIL)) < 0) {
                weights[1] = 1000 * (++i[0]) / size;
                return true;
            }
            return false;
        });
        if (!anyMatch) {
            return new GenericResult<>("Wrong duration: %d min, required: %s.", fileDuration.toMinutes(),
                    durations.stream().map(duration -> duration.toMinutes() + "min").collect(Collectors.joining("/")));
        }

        long length = file.length();
        if (length == 0) {
            return new GenericResult<>("Can't get size.");
        }
        long targetSize = fileDuration.getSeconds() * MOVIE_STANDARD_KBPS * 1024;
        if (length < targetSize * MOVIE_SIZE_FLOOR) {
            return new GenericResult<>("Too small size: %s, standard: %s.", printSize(length), printSize(targetSize));
        }
        if (length > targetSize * MOVIE_SIZE_CEIL) {
            return new GenericResult<>("Too big size: %s, standard: %s.", printSize(length), printSize(targetSize));
        }
        if (length < targetSize) {
            weights[2] = (int) (length * 10 / targetSize);
        } else {
            weights[2] = (int) (targetSize * 10 / length);
        }
        return GenericResult.of(Arrays.stream(weights).sum());
    }

    /**
     * @param size in Byte
     */
    private String printSize(double size) {
        for (String unit : new String[]{"B", "KB", "MB", "GB", "TB"}) {
            if (size < 1024) {
                return String.format("%.2f %s", size, unit);
            }
            size /= 1024;
        }
        return String.format("%.2f PB", size);
    }

    /**
     * Obtains corresponding location of the given entity, based on {@link #cdn}.
     *
     * @return location of the given entity
     */
    @Nonnull
    private String getLocation(SubjectEntity entity) {
        Objects.requireNonNull(entity, "Given entity mustn't be null.");
        Objects.requireNonNull(entity.getLanguages(), "Languages of subject " + entity.getId() + " mustn't be null.");
        Objects.requireNonNull(entity.getYear(), "Year of subject " + entity.getId() + " mustn't be null.");
        Objects.requireNonNull(entity.getTitle(), "Title of subject " + entity.getId() + " mustn't be null.");

        StringBuilder builder = new StringBuilder()
                .append(cdn)
                .append(File.separator);
        if (entity instanceof MovieEntity) {
            builder.append(MOVIE_DIR);
        } else {
            builder.append(TV_DIR);
        }
        builder.append(File.separator);

        LanguageEnum language = entity.getLanguages().get(0);
        if (language.ordinal() <= LanguageEnum.TH.ordinal()) {
            builder.append(String.format("%02d", language.ordinal()))
                    .append(SignEnum.SPACE)
                    .append(language.getTitle());
        } else {
            builder.append("99")
                    .append(SignEnum.SPACE)
                    .append("其他");
        }
        builder.append(File.separator).append(entity.getYear());
        return builder.append(NAME_SEPARATOR).append(StringUtilsExt.toFilename(entity.getTitle())).toString();
    }

    public enum ArchivedStatus {
        /**
         * Statuses of archiving
         */
        NONE_FOUND,
        ADDED,
        DOWNLOADING,
        NONE_DOWNLOADED,
        NO_QUALIFIED,
        ARCHIVED;

        @Setter
        private String msg;

        @Override
        public String toString() {
            return name() + (msg == null ? "" : ("{" + msg + '}'));
        }
    }
}
