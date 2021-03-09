package wsg.tools.boot.dao.api.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.common.util.OtherHttpResponseException;
import wsg.tools.boot.common.util.SiteUtilExt;
import wsg.tools.boot.config.SiteManager;
import wsg.tools.boot.dao.api.intf.ImdbView;
import wsg.tools.boot.dao.api.intf.SubjectAdapter;
import wsg.tools.boot.dao.jpa.mapper.IdRelationRepository;
import wsg.tools.boot.pojo.entity.subject.IdRelationEntity;
import wsg.tools.boot.pojo.error.UnknownTypeException;
import wsg.tools.internet.movie.common.enums.DoubanCatalog;
import wsg.tools.internet.movie.common.enums.DoubanMark;
import wsg.tools.internet.movie.douban.BaseDoubanSubject;
import wsg.tools.internet.movie.imdb.ImdbEpisode;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;
import wsg.tools.internet.movie.imdb.ImdbMovie;
import wsg.tools.internet.movie.imdb.ImdbRepository;
import wsg.tools.internet.movie.imdb.ImdbSeries;
import wsg.tools.internet.movie.imdb.ImdbTitle;
import wsg.tools.internet.movie.imdb.OmdbEpisode;
import wsg.tools.internet.movie.imdb.OmdbMovie;
import wsg.tools.internet.movie.imdb.OmdbSeason;
import wsg.tools.internet.movie.imdb.OmdbSeries;
import wsg.tools.internet.movie.imdb.OmdbSite;
import wsg.tools.internet.movie.imdb.OmdbTitle;

/**
 * @author Kingen
 * @since 2020/6/26
 */
@Slf4j
@Component
public class SubjectAdapterImpl implements SubjectAdapter {

    private final SiteManager manager;
    private final IdRelationRepository relationRepository;

    @Autowired
    public SubjectAdapterImpl(IdRelationRepository relationRepository, SiteManager manager) {
        this.manager = manager;
        this.relationRepository = relationRepository;
    }

    @Override
    public BaseDoubanSubject doubanSubject(long dbId)
        throws NotFoundException, OtherHttpResponseException {
        BaseDoubanSubject subject = SiteUtilExt
            .ifNotFound(dbId, manager.doubanSite()::findById, "Douban subject of " + dbId);
        if (subject.getImdbId() != null) {
            saveIdRelation(dbId, subject.getImdbId());
        }
        return subject;
    }

    @Override
    public Long getDbIdByImdbId(String imdbId)
        throws NotFoundException, OtherHttpResponseException {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isPresent()) {
            return optional.get().getDbId();
        }
        Long dbIdByImdbId = SiteUtilExt.found(imdbId, manager.doubanSite()::getDbIdByImdbId);
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
    public Map<Long, LocalDate> collectUserSubjects(long userId, LocalDate since, DoubanMark mark)
        throws NotFoundException, OtherHttpResponseException {
        return SiteUtilExt.ifNotFound(userId, since, mark, (id, date, ma) -> manager.doubanSite()
            .collectUserSubjects(id, date, DoubanCatalog.MOVIE, ma), "Collections of " + userId);
    }

    @Override
    public ImdbView imdbView(String imdbId) throws NotFoundException, OtherHttpResponseException {
        Objects.requireNonNull(imdbId);
        ImdbRepository<? extends ImdbIdentifier> imdbRepository = manager.imdbRepository();
        if (imdbRepository instanceof OmdbSite) {
            OmdbSite omdbSite = (OmdbSite) imdbRepository;
            OmdbTitle omdbTitle = SiteUtilExt
                .ifNotFound(imdbId, omdbSite::findById, "OMDb title of " + imdbId);
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
                    OmdbSeason season = SiteUtilExt.ifNotFound(imdbId, i, omdbSite::season,
                        "OMDb season " + i + " of " + imdbId);
                    List<OmdbSeason.Episode> episodes = season.getEpisodes();
                    int maxEpisode =
                        episodes.stream().mapToInt(OmdbSeason.Episode::getCurrentEpisode).max()
                            .orElseThrow();
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
        ImdbIdentifier identifier = SiteUtilExt.ifNotFound(imdbId, imdbRepository::findById,
            "IMDb title of " + imdbId);
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
}
