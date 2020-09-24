package wsg.tools.boot.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import wsg.tools.boot.dao.jpa.mapper.IdRelationRepository;
import wsg.tools.boot.pojo.base.ListResult;
import wsg.tools.boot.pojo.entity.IdRelationEntity;
import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.result.SiteResult;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.resource.entity.AbstractResource;
import wsg.tools.internet.resource.site.BdFilmSite;
import wsg.tools.internet.resource.site.XlcSite;
import wsg.tools.internet.resource.site.Y80sSite;
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
import java.util.*;

/**
 * Configuration for video to get instances of video sites.
 *
 * @author Kingen
 * @since 2020/6/26
 */
@Slf4j
@Configuration
@PropertySource("classpath:config/private/video.properties")
public class VideoAdapter implements InitializingBean {

    private static final String DOUBAN_RESOURCE = "douban subject";
    private static final String IMDB_RESOURCE = "imdb title";
    private final Y80sSite y80sSite = new Y80sSite();
    private final XlcSite xlcSite = new XlcSite();
    private final BdFilmSite bdFilmSite = new BdFilmSite();
    private final ImdbSite imdbSite = new ImdbSite();
    private final DoubanSite doubanSite = new DoubanSite();
    @Value("${omdb.api.key}")
    private String omdbApiKey;
    private OmdbSite omdbSite;
    private IdRelationRepository relationRepository;

    public ListResult<AbstractResource> searchResources(MovieEntity entity) {
        List<AbstractResource> resources = new ArrayList<>();
        try {
            resources.addAll(y80sSite.collectMovie(entity.getTitle(), entity.getYear(), entity.getDbId()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        try {
            resources.addAll(xlcSite.collectMovie(entity.getTitle(), entity.getYear()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        try {
            resources.addAll(bdFilmSite.collectMovie(entity.getTitle(), entity.getImdbId(), entity.getDbId()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new ListResult<>(resources);
    }

    public SiteResult<BaseDoubanSubject> doubanSubject(long dbId) throws IOException {
        try {
            BaseDoubanSubject subject = doubanSite.subject(dbId);
            return new SiteResult<>(subject);
        } catch (HttpResponseException e) {
            return new SiteResult<>(doubanSite, DOUBAN_RESOURCE, String.valueOf(dbId), e);
        }
    }

    public Long getDbIdByImdbId(String imdbId) {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isPresent()) {
            return optional.get().getDbId();
        }
        Long dbId;
        try {
            dbId = doubanSite.getDbIdByImdbId(imdbId);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
        if (dbId != null) {
            IdRelationEntity entity = new IdRelationEntity();
            entity.setImdbId(imdbId);
            entity.setDbId(dbId);
            relationRepository.insert(entity);
        }
        return dbId;
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
        omdbSite = new OmdbSite(omdbApiKey);
    }

    @Autowired
    public void setRelationRepository(IdRelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }
}
