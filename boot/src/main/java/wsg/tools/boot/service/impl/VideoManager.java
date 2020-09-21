package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import wsg.tools.boot.dao.jpa.mapper.SubjectRepository;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.StringUtilsExt;
import wsg.tools.internet.resource.download.Downloader;
import wsg.tools.internet.resource.site.AbstractVideoResourceSite;
import wsg.tools.internet.video.enums.LanguageEnum;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
    private static final int MOVIE_DURATION_FLOOR = -30;
    private static final int MOVIE_DURATION_CEIL = 90;

    static {
        System.setProperty("java.awt.headless", "false");
    }

    private final Semaphore semaphore = new Semaphore(0);
    @Value("${video.cdn}")
    private String cdn;
    private SubjectRepository subjectRepository;
    private ThreadPoolTaskExecutor executor;

    public Map<SubjectEntity, GenericResult<File>> collect() throws ExecutionException, InterruptedException {
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

        Future<Map<SubjectEntity, GenericResult<File>>> task = executor.submit(() -> {
            Map<SubjectEntity, GenericResult<File>> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
            for (SubjectEntity entity : subjectRepository.findAll()) {
                if (entity instanceof MovieEntity) {
                    try {
                        GenericResult<File> result = getFile((MovieEntity) entity);
                        if (!result.isSuccess()) {
                            log.info(result.getMessage());
                            map.put(entity, result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            closed.release();
            return map;
        });
        return task.get();
    }

    /**
     * Obtains corresponding file of the given movie.
     * <p>
     * Firstly, locate the file under {@link #cdn}. Otherwise, find under temporary directory.
     * If still not found, search by {@link AbstractVideoResourceSite}s and download by {@link Downloader}.
     */
    public final GenericResult<File> getFile(MovieEntity entity) {
        String location = getLocation(entity);
        for (String suffix : Downloader.VIDEO_SUFFIXES) {
            File file = new File(location + SignEnum.DOT + suffix);
            if (file.isFile()) {
                log.info("Find file: {}.", file.getPath());
                return GenericResult.of(file);
            }
        }
        File tempDir = new File(String.join(File.separator,
                cdn, "Temp", entity.getId() + "" + SignEnum.UNDERSCORE + entity.getTitle()));
        if (tempDir.isDirectory() && Objects.requireNonNull(tempDir.list()).length > 0) {
            if (FileUtils.listFiles(tempDir, Downloader.THUNDER_FILE_SUFFIXES, true).isEmpty()) {
                Map.Entry<File, GenericResult<Integer>> max = FileUtils.listFiles(tempDir, Downloader.VIDEO_SUFFIXES, true).stream()
                        .collect(Collectors.toMap(file -> file, file -> this.weight(file, entity.getDurations())))
                        .entrySet().stream()
                        .max(Comparator.comparingInt(e -> (e.getValue().isSuccess() ? e.getValue().getData() : -1))).orElseThrow();
                GenericResult<Integer> result = max.getValue();
                if (!result.isSuccess()) {
                    return new GenericResult<>("No qualified files downloaded for %s. Check manually.", entity.getTitle());
                }
                File srcFile = max.getKey();
                File destFile = new File(location + srcFile.getName().substring(srcFile.getName().lastIndexOf(SignEnum.DOT.getC())));
                try {
                    FileUtils.moveFile(srcFile, destFile);
                    FileUtils.deleteDirectory(tempDir);
                } catch (IOException e) {
                    throw AssertUtils.runtimeException(e);
                }
                return GenericResult.of(destFile);
            } else {
                return new GenericResult<>("None files of %s found, resources downloading.", entity.getTitle());
            }
        }
        int count = Downloader.downloadMovie(tempDir, entity.getTitle(), entity.getYear(), entity.getDbId());
        if (count > 0) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw AssertUtils.runtimeException(e);
            }
            return new GenericResult<>("None files of %s found, %d resources added to download.", entity.getTitle(), count);
        } else {
            if (!tempDir.delete()) {
                log.error("Can't delete temp directory {}.", tempDir.getPath());
            }
            return new GenericResult<>("None files of %s found, none resources found either.", entity.getTitle());
        }
    }

    private GenericResult<Integer> weight(@Nonnull File file, final List<Duration> durations) {
        int[] weights = new int[3];
        String name = file.getName();
        if (Downloader.isSuffix(name, Downloader.GOOD_VIDEO_SUFFIXES)) {
            weights[0] = 100;
        } else if (Downloader.isSuffix(name, Downloader.OTHER_VIDEO_SUFFIXES)) {
            weights[0] = 50;
        } else {
            return new GenericResult<>("Not permit suffix of file: %s.", file.getPath());
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
                return new GenericResult<>("Can't get duration of file: %s, .", file.getPath());
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
            return new GenericResult<>("Wrong duration %d min of file: %s, .", fileDuration.toMinutes(), file.getPath());
        }

        long length = file.length();
        if (length == 0) {
            return new GenericResult<>("Can't get size of file: %s.", file.getPath());
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
    private String getLocation(MovieEntity entity) {
        Objects.requireNonNull(entity, "Given entity mustn't be null.");
        Objects.requireNonNull(entity.getLanguages(), "Languages of subject " + entity.getId() + " mustn't be null.");
        Objects.requireNonNull(entity.getYear(), "Year of subject " + entity.getId() + " mustn't be null.");
        Objects.requireNonNull(entity.getTitle(), "Title of subject " + entity.getId() + " mustn't be null.");

        StringBuilder builder = new StringBuilder()
                .append(cdn)
                .append(File.separator)
                .append(MOVIE_DIR)
                .append(File.separator);
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
        builder.append(File.separator)
                .append(entity.getYear());
        if (StringUtils.isNotBlank(entity.getOriginalTitle())) {
            builder.append(NAME_SEPARATOR).append(StringUtilsExt.toFilename(entity.getOriginalTitle()));
        }
        return builder.append(NAME_SEPARATOR).append(StringUtilsExt.toFilename(entity.getTitle())).toString();
    }

    @Autowired
    public void setExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setSubjectRepository(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
}
