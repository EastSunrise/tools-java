package wsg.tools.boot.dao.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.config.PathConfiguration;
import wsg.tools.boot.dao.api.intf.ImdbView;
import wsg.tools.boot.dao.api.intf.SubjectAdapter;
import wsg.tools.boot.dao.jpa.mapper.IdRelationRepository;
import wsg.tools.boot.pojo.entity.subject.IdRelationEntity;
import wsg.tools.boot.pojo.error.UnknownTypeException;
import wsg.tools.common.util.function.throwable.ThrowableFunction;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.SiteStatus;
import wsg.tools.internet.base.SiteStatusException;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.douban.BaseDoubanSubject;
import wsg.tools.internet.video.site.douban.DoubanSite;
import wsg.tools.internet.video.site.imdb.*;

import java.time.LocalDate;
import java.util.*;

/**
 * @param <S> type of {@link ImdbRepository}
 * @author Kingen
 * @since 2020/6/26
 */
@Slf4j
@Component
public class SubjectAdapterImpl<S extends BaseSite & ImdbRepository<? extends ImdbIdentifier>> implements SubjectAdapter, DisposableBean {

    private final DoubanSite doubanSite = DoubanSite.getInstance();
    private final IdRelationRepository relationRepository;
    private final S imdbRepository;

    @Autowired
    @SuppressWarnings("unchecked")
    public SubjectAdapterImpl(IdRelationRepository relationRepository, PathConfiguration configuration) {
        this.relationRepository = relationRepository;
        S imdbRepository;
        try {
            SiteStatus.Status.validateStatus(ImdbSite.class);
            imdbRepository = (S) ImdbSite.getInstance();
        } catch (SiteStatusException ignored) {
            String omdbKey = configuration.getOmdbKey();
            if (StringUtils.isNotBlank(omdbKey)) {
                imdbRepository = (S) new OmdbSite(omdbKey);
            } else {
                imdbRepository = (S) ImdbCnSite.getInstance();
            }
        }
        this.imdbRepository = imdbRepository;
    }

    @Override
    public BaseDoubanSubject doubanSubject(long dbId) throws HttpResponseException, NotFoundException {
        BaseDoubanSubject subject = handleException(dbId, doubanSite::subject, "Douban subject of " + dbId);
        if (subject.getImdbId() != null) {
            saveIdRelation(dbId, subject.getImdbId());
        }
        return subject;
    }

    @Override
    public Long getDbIdByImdbId(String imdbId) throws HttpResponseException, NotFoundException {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isPresent()) {
            return optional.get().getDbId();
        }
        Long dbIdByImdbId = doubanSite.getDbIdByImdbId(imdbId);
        if (dbIdByImdbId != null) {
            return saveIdRelation(dbIdByImdbId, imdbId);
        }
        throw new NotFoundException("Can't find douban ID by IMDb ID: " + imdbId);
    }

    private long saveIdRelation(long dbId, String imdbId) {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isEmpty()) {
            IdRelationEntity entity = new IdRelationEntity();
            entity.setImdbId(imdbId);
            entity.setDbId(dbId);
            relationRepository.insert(entity);
        }
        return dbId;
    }

    @Override
    public Map<Long, LocalDate> collectUserSubjects(long userId, LocalDate since, MarkEnum mark) throws HttpResponseException, NotFoundException {
        return handleException(userId, user -> doubanSite.collectUserSubjects(user, since, CatalogEnum.MOVIE, mark), "collections of " + userId);
    }

    @Override
    public ImdbView imdbView(String imdbId) throws HttpResponseException, NotFoundException {
        Objects.requireNonNull(imdbId);
        if (imdbRepository instanceof OmdbSite) {
            OmdbSite omdbSite = (OmdbSite) imdbRepository;
            OmdbTitle omdbTitle = handleException(imdbId, omdbSite::getItemById, "OMDb title of " + imdbId);
            if (omdbTitle instanceof OmdbMovie) {
                return new OmdbMovieAdapter((OmdbMovie) omdbTitle);
            }
            if (omdbTitle instanceof OmdbSeries) {
                Integer totalSeasons = ((OmdbSeries) omdbTitle).getTotalSeasons();
                if (totalSeasons == null) {
                    totalSeasons = 0;
                }
                List<String[]> allEpisodes = new ArrayList<>();
                for (int i = 1; i <= totalSeasons; i++) {
                    OmdbSeason season = handleException(i, s -> omdbSite.season(imdbId, s), "OMDb season " + i + " of " + imdbId);
                    List<OmdbSeason.Episode> episodes = season.getEpisodes();
                    int maxEpisode = episodes.stream().mapToInt(OmdbSeason.Episode::getCurrentEpisode).max().orElseThrow();
                    String[] episodeIds = new String[maxEpisode + 1];
                    episodes.forEach(e -> episodeIds[e.getCurrentEpisode()] = e.getImdbId());
                    allEpisodes.add(episodeIds);
                }
                return new OmdbSeriesAdapter((OmdbSeries) omdbTitle, allEpisodes);
            }
            if (omdbTitle instanceof OmdbEpisode) {
                return new OmdbEpisodeAdapter((OmdbEpisode) omdbTitle);
            }
            throw new UnknownTypeException(omdbTitle.getClass());
        }
        ImdbIdentifier identifier = handleException(imdbId, imdbRepository::getItemById, "IMDb title of " + imdbId);
        if (identifier instanceof ImdbTitle) {
            if (identifier instanceof ImdbMovie) {
                return new ImdbMovieAdapter((ImdbMovie) identifier);
            }
            if (identifier instanceof ImdbSeries) {
                return new ImdbSeriesAdapter((ImdbSeries) identifier);
            }
            if (identifier instanceof ImdbEpisode) {
                return new ImdbEpisodeAdapter((ImdbEpisode) identifier);
            }
        }
        throw new UnknownTypeException(identifier.getClass());
    }

    private <T, R> R handleException(T t, ThrowableFunction<T, R, HttpResponseException> function, String msg) throws NotFoundException, HttpResponseException {
        try {
            return function.apply(t);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException("Not found: " + msg);
            }
            throw e;
        }
    }

    @Override
    public void destroy() throws Exception {
        doubanSite.close();
        imdbRepository.close();
    }
}
