package wsg.tools.boot.dao.api;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import wsg.tools.internet.video.site.DoubanSite;
import wsg.tools.internet.video.site.ImdbSite;
import wsg.tools.internet.video.site.OmdbSite;

/**
 * Configuration for video to get instances of video sites.
 *
 * @author Kingen
 * @since 2020/6/26
 */
@Configuration
@PropertySource("classpath:config/private/video.properties")
public class VideoConfig implements InitializingBean {

    private ImdbSite imdbSite;

    @Value("${douban.api.key}")
    private String doubanApiKey;
    private DoubanSite doubanSite;

    @Value("${omdb.api.key}")
    private String omdbApiKey;
    private OmdbSite omdbSite;

    public DoubanSite getDoubanSite() {
        return doubanSite;
    }

    public OmdbSite getOmdbSite() {
        return omdbSite;
    }

    public ImdbSite getImdbSite() {
        return imdbSite;
    }

    @Override
    public void afterPropertiesSet() {
        doubanSite = new DoubanSite(doubanApiKey);
        omdbSite = new OmdbSite(omdbApiKey);
        imdbSite = new ImdbSite();
    }
}
