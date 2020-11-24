package wsg.tools.boot.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.entity.subject.SubjectEntity;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.video.enums.LanguageEnum;

import java.io.File;
import java.util.Objects;

/**
 * Configurations for video.
 *
 * @author Kingen
 * @since 2020/11/21
 */
@Configuration
@PropertySource("classpath:config/private/video.properties")
public class PathConfiguration implements InitializingBean, WebMvcConfigurer {

    private static final SignEnum NAME_SEPARATOR = SignEnum.UNDERSCORE;
    private static final String MOVIE_DIR = "01 Movies";
    private static final String TV_DIR = "02 TV";

    @Value("${video.cdn}")
    private String cdn;

    @Value("${video.tmpdir}")
    private String tmpdir;

    public String getLocation(MovieEntity entity) {
        return getLocation0(MOVIE_DIR, entity);
    }

    public String getLocation(SeriesEntity entity) {
        return getLocation0(TV_DIR, entity);
    }

    public String getLocation(SeasonEntity season) {
        SeriesEntity series = season.getSeries();
        String location = getLocation(series);
        if (series.getSeasonsCount() > 1) {
            location += File.separator + String.format("S%02d", season.getCurrentSeason());
        }
        return location;
    }

    public File tmpdir(SubjectEntity entity) {
        return new File(tmpdir, entity.getId() + "_" + entity.getTitle());
    }

    /**
     * Only for {@link MovieEntity} and {@link SeriesEntity}.
     */
    private String getLocation0(String dir, SubjectEntity entity) {
        Objects.requireNonNull(entity, "Given entity mustn't be null.");
        Objects.requireNonNull(entity.getLanguages(), "Languages of subject " + entity.getId() + " mustn't be null.");
        Objects.requireNonNull(entity.getYear(), "Year of subject " + entity.getId() + " mustn't be null.");
        Objects.requireNonNull(entity.getTitle(), "Title of subject " + entity.getId() + " mustn't be null.");

        StringBuilder builder = new StringBuilder().append(cdn).append(File.separator).append(dir).append(File.separator);
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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/cdn/**").addResourceLocations("file:" + new File(cdn).getPath() + File.separator);
        registry.addResourceHandler("/tmp/**").addResourceLocations("file:" + new File(tmpdir).getPath() + File.separator);
    }

    @Override
    public void afterPropertiesSet() {
        File file = new File(this.cdn);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Not a valid cdn: " + cdn);
        }
        file = new File(this.tmpdir);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Not a valid tmpdir: " + tmpdir);
        }
    }
}
