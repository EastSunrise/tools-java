package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import wsg.tools.boot.dao.api.VideoAdapter;
import wsg.tools.boot.dao.jpa.mapper.SeasonRepository;
import wsg.tools.boot.dao.jpa.mapper.SeriesRepository;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.entity.*;
import wsg.tools.boot.pojo.enums.ArchivedStatus;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.VideoManager;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.StringUtilsExt;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.video.enums.LanguageEnum;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Semaphore;
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
@PropertySource("classpath:config/private/video.properties")
public class VideoManagerImpl extends BaseServiceImpl implements VideoManager {

    private static final SignEnum NAME_SEPARATOR = SignEnum.UNDERSCORE;
    private static final String MOVIE_DIR = "01 Movies";
    private static final String TV_DIR = "02 TV";
    private static final Filetype[] GOOD_VIDEO_SUFFIXES = new Filetype[]{Filetype.MP4, Filetype.MKV};
    private static final Pattern EPISODE_FILENAME_REGEX = Pattern.compile("[^\\d]+(77ds)?[^\\d]+(?<e>\\d+)[^\\d]*(v2_hd)?[^\\d]*(\\(1\\))?");

    /**
     * standard bps of files of a movie in KB/s.
     */
    private static final int STANDARD_BIT_RATE = 250;
    /**
     * Permit size of a file mustn't be half smaller nor four bigger than the standard duration.
     */
    private static final double SIZE_FLOOR = 0.6;
    private static final double SIZE_CEIL = 2.5;
    /**
     * Permit errors of durations.
     */
    private static final Duration SINGLE_DURATION_ERROR = Duration.ofSeconds(60);
    private static final Duration SEASON_DURATION_ERROR = Duration.ofSeconds(300);

    private final SeriesRepository seriesRepository;
    private final SeasonRepository seasonRepository;
    private final VideoAdapter adapter;
    @Value("${video.tmpdir}")
    private String tmpdir;
    @Value("${video.cdn}")
    private String cdn;

    public VideoManagerImpl(SeriesRepository seriesRepository, SeasonRepository seasonRepository, VideoAdapter adapter) {
        this.seriesRepository = seriesRepository;
        this.seasonRepository = seasonRepository;
        this.adapter = adapter;
    }

    @Override
    public final Optional<File> getFile(MovieEntity movie) {
        String location = getLocation(movie);
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
        String location = getLocation(series);
        File seriesDir = new File(location);
        if (seriesDir.isDirectory()) {
            return Optional.of(seriesDir);
        }
        return Optional.empty();
    }

    @Override
    public Optional<File> getFile(SeasonEntity season) {
        SeriesEntity series = seriesRepository.findById(season.getSeriesId()).orElseThrow();
        Optional<File> optional = getFile(series);
        if (series.getSeasonsCount() == 1) {
            return optional;
        }

        if (optional.isEmpty()) {
            return Optional.empty();
        }
        File dir = new File(optional.get(), String.format("S%02d", season.getCurrentSeason()));
        if (dir.isDirectory()) {
            return Optional.of(dir);
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
        String location = optional.get() + File.separator + String.format(format, season.getCurrentSeason());
        for (Filetype type : Filetype.videoTypes()) {
            File file = new File(location + SignEnum.DOT + type.suffix());
            if (file.isFile()) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    /**
     * Firstly, locate the file under {@link #cdn}. Otherwise, find under temporary directory.
     * If still not found, search download by {@link VideoAdapter#download(MovieEntity, File)}.
     */
    @Override
    public final ArchivedStatus archive(MovieEntity movie) {
        Optional<File> optional = getFile(movie);
        if (optional.isPresent()) {
            return ArchivedStatus.ARCHIVED;
        }

        log.info("Archiving for {}.", movie.getTitle());
        String tmpName = movie.getId() + "" + SignEnum.UNDERSCORE + movie.getTitle();
        File tempDir = new File(String.join(File.separator, tmpdir, tmpName));
        if (tempDir.isDirectory()) {
            if (Objects.requireNonNull(tempDir.list()).length == 0) {
                return ArchivedStatus.NONE_DOWNLOADED;
            }
            if (!FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.downloadingTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
                return ArchivedStatus.DOWNLOADING;
            }

            Collection<File> files = FileUtils.listFiles(tempDir, Filetype.fileFilter(Filetype.videoTypes()), TrueFileFilter.INSTANCE);
            GenericResult<File> chosen = choose(files, movie.getDurations(), SINGLE_DURATION_ERROR);
            if (!chosen.isSuccess()) {
                ArchivedStatus status = ArchivedStatus.NO_QUALIFIED;
                status.setMsg(chosen.error());
                return status;
            }
            File chosenFile = chosen.get();
            File destFile = new File(getLocation(movie) + chosenFile.getName().substring(chosenFile.getName().lastIndexOf(SignEnum.DOT.getC())));
            try {
                FileUtils.moveFile(chosenFile, destFile);
                FileUtils.deleteDirectory(tempDir);
            } catch (IOException e) {
                throw AssertUtils.runtimeException(e);
            }
            return ArchivedStatus.ARCHIVED;
        }

        if (adapter.download(movie, tempDir) == 0) {
            return ArchivedStatus.NONE_FOUND;
        }
        return ArchivedStatus.ADDED;
    }

    /**
     * Firstly, locate the file under {@link #cdn}. Otherwise, find under temporary directory.
     * If still not found, search and download by {@link VideoAdapter#download(String, SeasonEntity, File)}.
     */
    @Override
    public ArchivedStatus archive(SeasonEntity season) {
        SeriesEntity series = seriesRepository.findById(season.getSeriesId()).orElseThrow();
        File seriesDir = new File(getLocation(series));
        File seasonDir = series.getSeasonsCount() == 1 ? seriesDir
                : new File(seriesDir, String.format("S%02d", season.getCurrentSeason()));
        if (seasonDir.isDirectory()) {
            return ArchivedStatus.ARCHIVED;
        }

        log.info("Archiving for {}.", season.getTitle());
        File tempDir = new File(tmpdir, season.getId() + "" + SignEnum.UNDERSCORE + season.getTitle());
        if (tempDir.isDirectory()) {
            if (Objects.requireNonNull(tempDir.list()).length == 0) {
                return ArchivedStatus.NONE_DOWNLOADED;
            }
            if (!FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.downloadingTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
                return ArchivedStatus.DOWNLOADING;
            }
            return archiveSeason(tempDir, seasonDir, season);
        }

        long count = adapter.download(series.getTitle(), season, tempDir);
        if (count == 0) {
            return ArchivedStatus.NONE_FOUND;
        }
        return ArchivedStatus.ADDED;
    }

    /**
     * Firstly, locate the file under {@link #cdn}. Otherwise, find under temporary directory.
     * If still not found, search and download by {@link VideoAdapter#download(String, SeasonEntity, File)}.
     */
    @Override
    public final Map<Integer, ArchivedStatus> archive(SeriesEntity series, Semaphore semaphore) {
        List<SeasonEntity> seasons = seasonRepository.findAllBySeriesId(series.getId());
        Map<Integer, ArchivedStatus> statuses = new HashMap<>(series.getSeasonsCount());
        File seriesDir = new File(getLocation(series));

        for (SeasonEntity season : seasons) {
            int currentSeason = season.getCurrentSeason();
            File seasonDir = series.getSeasonsCount() == 1 ? seriesDir
                    : new File(seriesDir, String.format("S%02d", currentSeason));
            if (seasonDir.isDirectory()) {
                statuses.put(currentSeason, ArchivedStatus.ARCHIVED);
                continue;
            }

            log.info("Archiving for {}.", season.getTitle());
            File tempDir = new File(tmpdir, season.getId() + "" + SignEnum.UNDERSCORE + season.getTitle());
            if (tempDir.isDirectory()) {
                if (Objects.requireNonNull(tempDir.list()).length == 0) {
                    statuses.put(currentSeason, ArchivedStatus.NONE_DOWNLOADED);
                    continue;
                }
                if (!FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.downloadingTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
                    statuses.put(currentSeason, ArchivedStatus.DOWNLOADING);
                    continue;
                }
                statuses.put(currentSeason, archiveSeason(tempDir, seasonDir, season));
                continue;
            }

            long count = adapter.download(series.getTitle(), season, tempDir);
            if (count == 0) {
                statuses.put(currentSeason, ArchivedStatus.NONE_FOUND);
                continue;
            }
            statuses.put(currentSeason, ArchivedStatus.ADDED);
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw AssertUtils.runtimeException(e);
            }
        }

        return statuses;
    }

    private ArchivedStatus archiveSeason(File tempDir, File seasonDir, SeasonEntity season) {
        Collection<File> allFiles = FileUtils.listFiles(tempDir, Filetype.fileFilter(Filetype.videoTypes()), TrueFileFilter.INSTANCE);
        Map<Integer, List<File>> map = allFiles.stream().collect(Collectors.groupingBy(this::parseEpisode));
        Integer episodesCount = season.getEpisodesCount();
        Map<Integer, File> chosenFiles = new HashMap<>(episodesCount);
        List<String> fails = new ArrayList<>();
        Map<Integer, EpisodeEntity> episodes = season.getEpisodes().stream()
                .collect(Collectors.toMap(EpisodeEntity::getCurrentEpisode, episodeEntity -> episodeEntity));
        for (int currentEpisode = 1; currentEpisode <= episodesCount; currentEpisode++) {
            List<File> episodesFiles = map.get(currentEpisode);
            if (episodesFiles != null) {
                EpisodeEntity episode = episodes.get(currentEpisode);
                GenericResult<File> chosen;
                if (episode != null && episode.getDurations() != null) {
                    chosen = choose(episodesFiles, episode.getDurations(), SINGLE_DURATION_ERROR);
                } else {
                    chosen = choose(episodesFiles, season.getDurations(), SEASON_DURATION_ERROR);
                }
                if (chosen.isSuccess()) {
                    chosenFiles.put(currentEpisode, chosen.get());
                    continue;
                }
                fails.add(chosen.error());
            } else {
                fails.add("None files for episode " + (currentEpisode + 1));
            }
        }

        if (!fails.isEmpty()) {
            ArchivedStatus status = ArchivedStatus.LACKING;
            status.setMsg(String.join(Constants.LINE_SEPARATOR, fails));
            return status;
        }

        String format = "E%0" + (((int) Math.log10(season.getEpisodesCount())) + 1) + "d";
        for (Map.Entry<Integer, File> entry : chosenFiles.entrySet()) {
            File chosenFile = entry.getValue();
            String ext = chosenFile.getName().substring(chosenFile.getName().lastIndexOf(SignEnum.DOT.getC()));
            File destFile = new File(seasonDir, String.format(format, entry.getKey()) + ext);
            try {
                FileUtils.moveFile(chosenFile, destFile);
            } catch (IOException e) {
                throw AssertUtils.runtimeException(e);
            }
        }
        try {
            FileUtils.deleteDirectory(tempDir);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
        return ArchivedStatus.ARCHIVED;
    }

    private int parseEpisode(File file) {
        String name = file.getName();
        if (name.indexOf(SignEnum.DOT.getC()) > 0) {
            name = name.substring(0, name.lastIndexOf(SignEnum.DOT.getC()));
        }
        try {
            return Integer.parseInt(AssertUtils.matches(EPISODE_FILENAME_REGEX, name).group("e"));
        } catch (IllegalArgumentException e) {
            log.error(name);
            return -1;
        }
    }

    private GenericResult<File> choose(Collection<File> files, List<Duration> durations, final Duration error) {
        List<String> fails = new ArrayList<>();
        Optional<DefaultMapEntry<File, GenericResult<Long>>> max = files.stream()
                .map(file -> new DefaultMapEntry<>(file, this.weight(file, durations, error)))
                .filter(entry -> {
                    if (entry.getValue().isSuccess()) {
                        return true;
                    } else {
                        fails.add(String.format("Reason: %s File: %s", entry.getValue().error(), entry.getKey().getName()));
                        return false;
                    }
                })
                .max(Comparator.comparingLong(entry -> entry.getValue().get()));
        if (max.isEmpty()) {
            return new GenericResult<>(String.join(Constants.LINE_SEPARATOR, fails));
        }
        return GenericResult.of(max.get().getKey());
    }

    /**
     * Calculate weight of a file for the subject.
     *
     * @param file      file to calculate
     * @param durations standard durations of the subject
     */
    private GenericResult<Long> weight(@Nonnull File file, final List<Duration> durations, Duration error) {
        Filetype filetype = Filetype.typeOf(file.getName());
        if (filetype == null || !filetype.isVideo()) {
            return new GenericResult<>("Not a video file.");
        }
        boolean goodType = ArrayUtils.contains(GOOD_VIDEO_SUFFIXES, filetype);

        if (durations.size() < 1) {
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

        boolean anyMatch = false;
        int durationIndex = durations.size() - 1;
        while (durationIndex >= 0) {
            Duration duration = durations.get(durationIndex);
            if (fileDuration.minus(duration).abs().compareTo(error.abs()) < 0) {
                anyMatch = true;
                break;
            }
            durationIndex--;
        }
        if (!anyMatch) {
            return new GenericResult<>("Wrong duration: %d min, required: %s.", fileDuration.toMinutes(),
                    durations.stream().map(duration -> duration.toMinutes() + "min").collect(Collectors.joining("/")));
        }
        Duration durationError = fileDuration.minus(durations.get(durationIndex)).plus(error.abs());

        long length = file.length();
        if (length == 0) {
            return new GenericResult<>("Can't get size.");
        }
        long targetSize = fileDuration.getSeconds() * STANDARD_BIT_RATE * 1024;
        if (length < targetSize * SIZE_FLOOR) {
            return new GenericResult<>("Too small size: %s, standard: %s.", printSize(length), printSize(targetSize));
        }
        if (length > targetSize * SIZE_CEIL) {
            return new GenericResult<>("Too big size: %s, standard: %s.", printSize(length), printSize(targetSize));
        }
        double sizeRatio = length < targetSize ? length * 1.0 / targetSize : targetSize * 1.0 / length;

        return GenericResult.of(durationIndex * 1000 + durationError.getSeconds() + (goodType ? 30 : 0) + (int) (sizeRatio * 120));
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
     * Based on {@link #cdn}.
     */
    @Override
    public final String getLocation(SubjectEntity entity) {
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
}
