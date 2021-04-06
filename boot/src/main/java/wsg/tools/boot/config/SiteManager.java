package wsg.tools.boot.config;

import java.io.Closeable;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import wsg.tools.internet.common.SiteStatusException;
import wsg.tools.internet.common.SiteUtils;
import wsg.tools.internet.download.Downloader;
import wsg.tools.internet.download.FileExistStrategy;
import wsg.tools.internet.download.support.BasicDownloader;
import wsg.tools.internet.info.adult.licence.LicencePlateSite;
import wsg.tools.internet.info.adult.midnight.MidnightSite;
import wsg.tools.internet.movie.douban.DoubanSite;
import wsg.tools.internet.movie.imdb.ImdbCnSite;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;
import wsg.tools.internet.movie.imdb.ImdbRepository;
import wsg.tools.internet.movie.imdb.ImdbSite;
import wsg.tools.internet.movie.imdb.OmdbSite;
import wsg.tools.internet.movie.resource.BdMovieSite;
import wsg.tools.internet.movie.resource.GrapeSite;
import wsg.tools.internet.movie.resource.MovieHeavenSite;
import wsg.tools.internet.movie.resource.XlcSite;
import wsg.tools.internet.movie.resource.XlmSite;

/**
 * Management of all sites.
 *
 * @author Kingen
 * @since 2021/3/9
 */
@Configuration
public class SiteManager implements DisposableBean {

    private final PathConfiguration configuration;

    private DoubanSite doubanSite;
    private ImdbRepository<? extends ImdbIdentifier> imdbRepository;
    private BdMovieSite bdMovieSite;
    private MovieHeavenSite movieHeavenSite;
    private XlcSite xlcSite;
    private XlmSite xlmSite;
    private GrapeSite grapeSite;
    private Downloader downloader;
    private LicencePlateSite licencePlateSite;
    private MidnightSite midnightSite;

    @Autowired
    public SiteManager(PathConfiguration configuration) {
        this.configuration = configuration;
    }

    public DoubanSite doubanSite() {
        if (doubanSite == null) {
            doubanSite = new DoubanSite();
        }
        return doubanSite;
    }

    public ImdbRepository<? extends ImdbIdentifier> imdbRepository() {
        if (imdbRepository == null) {
            try {
                SiteUtils.validateStatus(ImdbSite.class);
                imdbRepository = new ImdbSite();
            } catch (SiteStatusException ignored) {
                String omdbKey = configuration.getOmdbKey();
                if (StringUtils.isNotBlank(omdbKey)) {
                    imdbRepository = new OmdbSite(omdbKey);
                } else {
                    imdbRepository = new ImdbCnSite();
                }
            }
        }
        return imdbRepository;
    }

    public BdMovieSite bdMovieSite() {
        if (bdMovieSite == null) {
            bdMovieSite = new BdMovieSite();
        }
        return bdMovieSite;
    }

    public MovieHeavenSite movieHeavenSite() {
        if (movieHeavenSite == null) {
            movieHeavenSite = new MovieHeavenSite();
        }
        return movieHeavenSite;
    }

    public XlcSite xlcSite() {
        if (xlcSite == null) {
            xlcSite = new XlcSite();
        }
        return xlcSite;
    }

    public XlmSite xlmSite() {
        if (xlmSite == null) {
            xlmSite = new XlmSite();
        }
        return xlmSite;
    }

    public GrapeSite grapeSite() {
        if (grapeSite == null) {
            grapeSite = new GrapeSite();
        }
        return grapeSite;
    }

    public LicencePlateSite licencePlateSite() {
        if (licencePlateSite == null) {
            licencePlateSite = new LicencePlateSite();
        }
        return licencePlateSite;
    }

    public MidnightSite midnightSite() {
        if (midnightSite == null) {
            midnightSite = new MidnightSite();
        }
        return midnightSite;
    }

    public Downloader downloader() {
        if (downloader == null) {
            downloader = new BasicDownloader().strategy(FileExistStrategy.FINISH);
        }
        return downloader;
    }

    @Override
    public void destroy() throws IOException {
        if (imdbRepository != null && imdbRepository instanceof Closeable) {
            ((Closeable) imdbRepository).close();
        }
        close(doubanSite, bdMovieSite, movieHeavenSite, xlcSite, xlmSite, grapeSite,
            licencePlateSite, midnightSite);
    }

    private void close(Closeable... sites) throws IOException {
        for (Closeable site : sites) {
            if (site != null) {
                site.close();
            }
        }
    }
}
