package wsg.tools.boot.dao.api.support;

import java.io.Closeable;
import java.io.IOException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import wsg.tools.boot.config.PathConfiguration;
import wsg.tools.boot.dao.api.ImdbRepo;
import wsg.tools.boot.dao.jpa.mapper.IdRelationRepository;
import wsg.tools.internet.download.Downloader;
import wsg.tools.internet.download.FileExistStrategy;
import wsg.tools.internet.download.support.BasicDownloader;
import wsg.tools.internet.info.adult.ggg.GggSite;
import wsg.tools.internet.info.adult.midnight.MidnightSite;
import wsg.tools.internet.info.adult.west.BabesTubeSite;
import wsg.tools.internet.info.adult.west.PornTubeSite;
import wsg.tools.internet.info.adult.wiki.CelebrityWikiSite;
import wsg.tools.internet.movie.douban.DoubanRepository;
import wsg.tools.internet.movie.douban.DoubanSite;
import wsg.tools.internet.movie.omdb.OmdbSite;
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
    private final IdRelationRepository relationRepository;

    private DoubanRepository doubanRepo;
    private ImdbRepo imdbRepo;
    private BdMovieSite bdMovieSite;
    private MovieHeavenSite movieHeavenSite;
    private XlcSite xlcSite;
    private XlmSite xlmSite;
    private GrapeSite grapeSite;
    private Downloader downloader;
    private MidnightSite midnightSite;
    private CelebrityWikiSite celebrityWikiSite;
    private GggSite gggSite;
    private PornTubeSite pornTubeSite;
    private BabesTubeSite babesTubeSite;

    @Autowired
    public SiteManager(PathConfiguration configuration, IdRelationRepository relationRepository) {
        this.configuration = configuration;
        this.relationRepository = relationRepository;
    }

    public DoubanRepository doubanRepo() {
        if (doubanRepo == null) {
            doubanRepo = new DoubanInterceptor(new DoubanSite(), relationRepository);
        }
        return doubanRepo;
    }

    public ImdbRepo imdbRepo() {
        if (imdbRepo == null) {
            String omdbKey = configuration.getOmdbKey();
            imdbRepo = new OmdbProxy(new OmdbSite(omdbKey));
        }
        return imdbRepo;
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

    public CelebrityWikiSite celebrityWikiSite() {
        if (celebrityWikiSite == null) {
            celebrityWikiSite = new CelebrityWikiSite();
        }
        return celebrityWikiSite;
    }

    public GggSite gggSite() {
        if (gggSite == null) {
            gggSite = new GggSite();
        }
        return gggSite;
    }

    public PornTubeSite pornTubeSite() {
        if (pornTubeSite == null) {
            pornTubeSite = new PornTubeSite();
        }
        return pornTubeSite;
    }

    public BabesTubeSite babesTubeSite() {
        if (babesTubeSite == null) {
            babesTubeSite = new BabesTubeSite();
        }
        return babesTubeSite;
    }

    @Override
    public void destroy() throws IOException {
        close(doubanRepo, imdbRepo, bdMovieSite, movieHeavenSite, xlcSite, xlmSite, grapeSite,
            midnightSite, celebrityWikiSite, gggSite, pornTubeSite,
            babesTubeSite);
    }

    private void close(Object... sites) throws IOException {
        for (Object site : sites) {
            if (site instanceof Closeable) {
                ((Closeable) site).close();
            }
        }
    }
}
