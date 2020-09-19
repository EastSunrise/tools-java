package wsg.tools.boot.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import wsg.tools.boot.pojo.result.SiteResult;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.entity.omdb.base.BaseOmdbTitle;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.DoubanSite;
import wsg.tools.internet.video.site.ImdbSite;
import wsg.tools.internet.video.site.OmdbSite;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration for video to get instances of video sites.
 *
 * @author Kingen
 * @since 2020/6/26
 */
@Slf4j
@Configuration
@PropertySource("classpath:config/private/video.properties")
public class VideoConfig implements InitializingBean {

    private static final String DOUBAN_RESOURCE = "douban subject";
    private static final String IMDB_RESOURCE = "imdb title";

    @Value("${omdb.api.key}")
    private String omdbApiKey;
    @Getter
    @Value("${site.cdn}")
    private String cdn;
    @Value("${webdriver.chrome}")
    private String chromeDriver;

    private ImdbSite imdbSite;
    private DoubanSite doubanSite;
    private OmdbSite omdbSite;

    public SiteResult<BaseDoubanSubject> doubanSubject(long dbId) throws IOException {
        try {
            BaseDoubanSubject subject = doubanSite.subject(dbId);
            return new SiteResult<>(subject);
        } catch (HttpResponseException e) {
            return new SiteResult<>(doubanSite, DOUBAN_RESOURCE, String.valueOf(dbId), e);
        }
    }

    public Long getDbIdByImdbId(String imdbId) {
        return doubanSite.getDbIdByImdbId(imdbId);
    }

    public Map<Long, LocalDate> collectUserSubjects(long userId, LocalDate since, MarkEnum mark) throws IOException {
        return doubanSite.collectUserSubjects(userId, since, CatalogEnum.MOVIE, mark);
    }

    public BaseImdbTitle imdbTitle(String imdbId) throws IOException {
        Objects.requireNonNull(imdbId);
        return imdbSite.title(imdbId);
    }

    public SiteResult<List<String[]>> episodes(String seriesId) throws IOException {
        Objects.requireNonNull(seriesId);
        try {
            return new SiteResult<>(imdbSite.episodes(seriesId));
        } catch (HttpResponseException e) {
            return new SiteResult<>(imdbSite, "episodes", seriesId, e);
        }
    }

    public SiteResult<BaseOmdbTitle> omdbTitle(String imdbId) throws IOException {
        Objects.requireNonNull(imdbId);
        try {
            return new SiteResult<>(omdbSite.title(imdbId));
        } catch (HttpResponseException e) {
            return new SiteResult<>(omdbSite, IMDB_RESOURCE, imdbId, e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        doubanSite = new DoubanSite();
        doubanSite.setCdn(cdn);
        imdbSite = new ImdbSite();
        imdbSite.setCdn(cdn);
        omdbSite = new OmdbSite(omdbApiKey);
        omdbSite.setCdn(cdn);
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, chromeDriver);
    }
}
