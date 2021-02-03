package wsg.tools.boot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.StringUtilsExt;

import javax.annotation.Nonnull;
import java.io.File;
import java.time.Year;

/**
 * Configurations for video.
 *
 * @author Kingen
 * @since 2020/11/21
 */
@Slf4j
@Configuration
@PropertySource("classpath:config/private/video.properties")
public class PathConfiguration implements InitializingBean, WebMvcConfigurer {

    private static final String NAME_SEPARATOR = "_";
    private static final String MOVIE_DIR = "01 Movies";
    private static final String TV_DIR = "02 TV";

    @Value("${video.cdn}")
    private String cdn;

    @Value("${video.tmpdir}")
    private String tmpdir;

    public String getLocation(MovieEntity entity) {
        return join(MOVIE_DIR, entity.getYear(), entity.getTitle());
    }

    public String getLocation(SeriesEntity entity) {
        return join(TV_DIR, entity.getYear(), entity.getTitle());
    }

    public String getLocation(SeasonEntity season) {
        SeriesEntity series = season.getSeries();
        String location = getLocation(series);
        if (series.getSeasonsCount() > 1) {
            location += File.separator + String.format("S%02d", season.getCurrentSeason());
        }
        return location;
    }

    public String getLocation(SeasonEntity season, int currentEpisode) {
        Integer episodeCount = AssertUtils.requireRange(currentEpisode, 1, season.getEpisodesCount() + 1);
        String format = "E%0" + (((int) Math.log10(episodeCount)) + 1) + "d";
        return getLocation(season) + File.separator + String.format(format, currentEpisode);
    }

    public File tmpdir(String title) {
        return new File(tmpdir, StringUtilsExt.toFilename(title));
    }

    /**
     * Join parts of the path.
     */
    private String join(@Nonnull String dir, @Nonnull Year year, @Nonnull String title) {
        return cdn + File.separator + dir + File.separator + year + NAME_SEPARATOR + StringUtilsExt.toFilename(title);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/cdn/**").addResourceLocations("file:" + new File(cdn).getPath() + File.separator);
        registry.addResourceHandler("/tmp/**").addResourceLocations("file:" + new File(tmpdir).getPath() + File.separator);
    }

    @Override
    public void afterPropertiesSet() {
        File file = new File(this.cdn);
        if (!file.isDirectory()) {
            log.warn("Not a valid cdn: " + cdn);
            this.cdn = System.getProperty("java.io.tmpdir");
        }
        file = new File(this.tmpdir);
        if (!file.isDirectory()) {
            log.warn("Not a valid tmpdir: " + tmpdir);
            this.tmpdir = System.getProperty("java.io.tmpdir");
        }
    }
}
