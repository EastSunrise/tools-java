package wsg.tools.boot.dao.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import wsg.tools.boot.dao.jpa.mapper.IdRelationRepository;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.entity.IdRelationEntity;
import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.entity.SeasonEntity;
import wsg.tools.common.io.Filetype;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.download.Downloader;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.resource.Ed2kResource;
import wsg.tools.internet.resource.entity.resource.HttpResource;
import wsg.tools.internet.resource.site.BdFilmSite;
import wsg.tools.internet.resource.site.MovieHeavenSite;
import wsg.tools.internet.resource.site.XlcSite;
import wsg.tools.internet.resource.site.Y80sSite;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.DoubanSite;
import wsg.tools.internet.video.site.ImdbSite;

import java.io.File;
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

    private static final String DOUBAN_SUBJECT = "douban subject";
    private static final String IMDB_TITLE = "imdb title";
    private final Y80sSite y80sSite = new Y80sSite();
    private final XlcSite xlcSite = new XlcSite();
    private final BdFilmSite bdFilmSite = new BdFilmSite();
    private final MovieHeavenSite movieHeavenSite = new MovieHeavenSite();

    private final ImdbSite imdbSite = new ImdbSite();
    private final DoubanSite doubanSite = new DoubanSite();
    private final Downloader downloader = new Thunder();
    private IdRelationRepository relationRepository;

    /**
     * Download the given movie to target directory.
     *
     * @return count of added resources
     */
    public long download(MovieEntity movie, File target) {
        int year = movie.getYear().getValue();
        Set<AbstractResource> resources = new HashSet<>();
        resources.addAll(y80sSite.collect(movie.getTitle(), year, null, movie.getDbId()));
        resources.addAll(xlcSite.collect(movie.getTitle(), year, null));
        resources.addAll(bdFilmSite.collectMovie(movie.getTitle(), movie.getImdbId(), movie.getDbId()));
        resources.addAll(movieHeavenSite.collect(movie.getTitle(), year, null));
        long count = resources.stream().filter(this::filterVideo)
                .filter(resource -> {
                    try {
                        return downloader.addTask(target, resource);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        return false;
                    }
                }).count();
        log.info("{} resources added to download.", count);
        return count;
    }

    /**
     * Download the given movie to target directory.
     *
     * @return count of added resources
     */
    public long download(SeasonEntity season, File target) {
        int year = season.getYear().getValue();
        String title = season.getSeries().getTitle();
        Set<AbstractResource> resources = new HashSet<>();
        resources.addAll(y80sSite.collect(title, year, season.getCurrentSeason(), season.getDbId()));
        resources.addAll(xlcSite.collect(title, year, season.getCurrentSeason()));
        resources.addAll(movieHeavenSite.collect(title, year, season.getCurrentSeason()));
        long count = resources.stream().filter(this::filterVideo)
                .filter(resource -> {
                    try {
                        return downloader.addTask(target, resource);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        return false;
                    }
                }).count();
        log.info("{} resources added to download.", count);
        return count;
    }

    private boolean filterVideo(AbstractResource resource) {
        if (resource instanceof Ed2kResource || resource instanceof HttpResource) {
            Filetype filetype = Filetype.getExternalType(resource.filename());
            return filetype != null && filetype.isVideo();
        }
        return true;
    }

    public GenericResult<BaseDoubanSubject> doubanSubject(long dbId) {
        try {
            return GenericResult.of(doubanSite.subject(dbId));
        } catch (NotFoundException e) {
            return new GenericResult<>(errorMsg(doubanSite, DOUBAN_SUBJECT, dbId, e));
        }
    }

    public GenericResult<Long> getDbIdByImdbId(String imdbId) {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isPresent()) {
            return GenericResult.of(optional.get().getDbId());
        }
        Long dbId = doubanSite.getDbIdByImdbId(imdbId);
        if (dbId != null) {
            IdRelationEntity entity = new IdRelationEntity();
            entity.setImdbId(imdbId);
            entity.setDbId(dbId);
            relationRepository.insert(entity);
            return GenericResult.of(dbId);
        } else {
            return new GenericResult<>("Can't find douban ID by IMDb ID: %s.", imdbId);
        }
    }

    public GenericResult<Map<Long, LocalDate>> collectUserSubjects(long userId, LocalDate since, MarkEnum mark) {
        try {
            return GenericResult.of(doubanSite.collectUserSubjects(userId, since, CatalogEnum.MOVIE, mark));
        } catch (NotFoundException e) {
            return new GenericResult<>(errorMsg(doubanSite, "douban user subjects", userId, e));
        }
    }

    public GenericResult<BaseImdbTitle> imdbTitle(String imdbId) {
        Objects.requireNonNull(imdbId);
        try {
            return GenericResult.of(imdbSite.title(imdbId));
        } catch (NotFoundException e) {
            return new GenericResult<>(errorMsg(imdbSite, IMDB_TITLE, imdbId, e));
        }
    }

    public GenericResult<List<String[]>> episodes(String seriesId) {
        Objects.requireNonNull(seriesId);
        try {
            return GenericResult.of(imdbSite.episodes(seriesId));
        } catch (NotFoundException e) {
            return new GenericResult<>(errorMsg(imdbSite, "episodes", seriesId, e));
        }
    }

    private String errorMsg(BaseSite site, String resource, Object resourceId, NotFoundException cause) {
        return String.format("Can't get %s from %s, id: %s, reason: %s.", resource, site.getName(), resourceId, cause.getReasonPhrase());
    }

    @Override
    public void afterPropertiesSet() {
    }

    @Autowired
    public void setRelationRepository(IdRelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }
}
