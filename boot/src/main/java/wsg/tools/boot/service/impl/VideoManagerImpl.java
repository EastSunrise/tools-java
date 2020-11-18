package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import wsg.tools.boot.dao.api.intf.ResourceAdapter;
import wsg.tools.boot.dao.jpa.mapper.SeasonRepository;
import wsg.tools.boot.pojo.entity.*;
import wsg.tools.boot.pojo.enums.ArchivedStatus;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.VideoManager;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.video.enums.LanguageEnum;

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

    private static final SignEnum NAME_SEPARATOR = SignEnum.UNDERSCORE;
    private static final String MOVIE_DIR = "01 Movies";
    private static final String TV_DIR = "02 TV";

    private final SeasonRepository seasonRepository;
    private final ResourceAdapter adapter;

    public VideoManagerImpl(SeasonRepository seasonRepository, ResourceAdapter adapter) {
        this.seasonRepository = seasonRepository;
        this.adapter = adapter;
    }

    @Override
    public final Optional<File> getFile(File cdn, MovieEntity movie) {
        String location = getLocation(cdn, movie);
        for (Filetype type : Filetype.videoTypes()) {
            File file = new File(location + SignEnum.DOT + type.suffix());
            if (file.isFile()) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    @Override
    public final Optional<File> getFile(File cdn, SeriesEntity series) {
        String location = getLocation(cdn, series);
        File seriesDir = new File(location);
        if (seriesDir.isDirectory()) {
            return Optional.of(seriesDir);
        }
        return Optional.empty();
    }

    @Override
    public Optional<File> getFile(File cdn, SeasonEntity season) {
        String location = getSeasonLocation(cdn, season);
        File seasonDir = new File(location);
        if (seasonDir.isDirectory()) {
            return Optional.of(seasonDir);
        }
        return Optional.empty();
    }

    @Override
    public Optional<File> getFile(File cdn, EpisodeEntity episode) {
        SeasonEntity season = seasonRepository.findById(episode.getSeasonId()).orElseThrow();
        Optional<File> optional = getFile(cdn, season);
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
    public final ArchivedStatus archive(File cdn, File tmpdir, MovieEntity movie) {
        Optional<File> optional = getFile(cdn, movie);
        if (optional.isPresent()) {
            return ArchivedStatus.ARCHIVED;
        }

        log.info("Archiving for {}.", movie.getTitle());
        String tmpName = movie.getId() + "" + SignEnum.UNDERSCORE + movie.getTitle();
        File tempDir = new File(tmpdir, tmpName);
        if (tempDir.isDirectory()) {
            if (Objects.requireNonNull(tempDir.list()).length == 0) {
                return ArchivedStatus.NONE_DOWNLOADED;
            }
            if (!FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.tmpTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
                return ArchivedStatus.DOWNLOADING;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("Target: ").append(getLocation(cdn, movie)).append(Constants.LINE_SEPARATOR);
            builder.append("Durations: ").append(StringUtils.join(movie.getDurations(), "/"));
            try {
                FileUtils.write(new File(tempDir, "info.txt"), builder.toString());
            } catch (IOException e) {
                throw AssertUtils.runtimeException(e);
            }
            return ArchivedStatus.TO_ARCHIVE;
        }

        if (adapter.download(adapter.search(movie), tempDir) == 0) {
            return ArchivedStatus.NONE_FOUND;
        }
        return ArchivedStatus.TO_DOWNLOAD;
    }

    @Override
    public ArchivedStatus archive(File cdn, File tmpdir, SeasonEntity season) {
        SeriesEntity series = season.getSeries();
        File seriesDir = new File(getLocation(cdn, series));
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
            if (!FileUtils.listFiles(tempDir, Filetype.fileFilter(Thunder.tmpTypes()), TrueFileFilter.INSTANCE).isEmpty()) {
                return ArchivedStatus.DOWNLOADING;
            }

            List<String> lines = new LinkedList<>();
            lines.add("Target: " + getSeasonLocation(cdn, season));
            lines.add("Season durations: " + StringUtils.join(season.getDurations(), "/"));
            lines.add("Episodes count: " + season.getEpisodesCount());
            String format = "Ep%0" + (((int) Math.log10(season.getEpisodesCount())) + 1) + "d";
            for (EpisodeEntity episode : season.getEpisodes()) {
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
            return ArchivedStatus.TO_ARCHIVE;
        }

        long count = adapter.download(adapter.search(season), tempDir);
        if (count == 0) {
            return ArchivedStatus.NONE_FOUND;
        }
        return ArchivedStatus.TO_DOWNLOAD;
    }

    /**
     * For {@link SeasonEntity}.
     */
    private String getSeasonLocation(File cdn, SeasonEntity season) {
        SeriesEntity series = season.getSeries();
        String location = getLocation(cdn, series);
        if (series.getSeasonsCount() > 1) {
            location += File.separator + String.format("S%02d", season.getCurrentSeason());
        }
        return location;
    }

    /**
     * Only for {@link MovieEntity} and {@link SeriesEntity}.
     */
    private String getLocation(File cdn, SubjectEntity entity) {
        Objects.requireNonNull(entity, "Given entity mustn't be null.");
        Objects.requireNonNull(entity.getLanguages(), "Languages of subject " + entity.getId() + " mustn't be null.");
        Objects.requireNonNull(entity.getYear(), "Year of subject " + entity.getId() + " mustn't be null.");
        Objects.requireNonNull(entity.getTitle(), "Title of subject " + entity.getId() + " mustn't be null.");

        StringBuilder builder = new StringBuilder()
                .append(cdn).append(File.separator);
        if (entity instanceof MovieEntity) {
            builder.append(MOVIE_DIR);
        } else if (entity instanceof SeriesEntity) {
            builder.append(TV_DIR);
        } else {
            throw new IllegalArgumentException("Unsupported entity of subject: " + entity.getClass().getName());
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
