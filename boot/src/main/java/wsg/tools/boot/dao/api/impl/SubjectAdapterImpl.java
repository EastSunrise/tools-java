package wsg.tools.boot.dao.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import wsg.tools.boot.dao.api.intf.SubjectAdapter;
import wsg.tools.boot.dao.jpa.mapper.IdRelationRepository;
import wsg.tools.boot.pojo.entity.subject.IdRelationEntity;
import wsg.tools.boot.pojo.error.SiteException;
import wsg.tools.boot.pojo.result.SingleResult;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.exception.LoginException;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.DoubanSite;
import wsg.tools.internet.video.site.ImdbSite;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Kingen
 * @since 2020/6/26
 */
@Slf4j
@Configuration
public class SubjectAdapterImpl implements SubjectAdapter {

    private static final String DOUBAN_SUBJECT = "douban subject";
    private static final String IMDB_TITLE = "imdb title";

    private final ImdbSite imdbSite = new ImdbSite();
    private final DoubanSite doubanSite = new DoubanSite();

    private IdRelationRepository relationRepository;

    @Override
    public SingleResult<BaseDoubanSubject> doubanSubject(long dbId) throws NotFoundException {
        try {
            BaseDoubanSubject subject = doubanSite.subject(dbId);
            if (subject.getImdbId() != null) {
                saveIdRelation(dbId, subject.getImdbId());
            }
            return SingleResult.of(subject);
        } catch (NotFoundException e) {
            throw new NotFoundException(errorMsg(doubanSite, DOUBAN_SUBJECT, dbId, e));
        }
    }

    @Override
    public SingleResult<Long> getDbIdByImdbId(String imdbId) throws SiteException, NotFoundException {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isPresent()) {
            return SingleResult.of(optional.get().getDbId());
        }
        Long dbId;
        try {
            dbId = doubanSite.getDbIdByImdbId(imdbId);
        } catch (LoginException e) {
            throw new SiteException(doubanSite.getName(), "Can't find douban ID by IMDb ID without logging in.");
        }
        if (dbId != null) {
            saveIdRelation(dbId, imdbId);
            return SingleResult.of(dbId);
        } else {
            throw new NotFoundException("Can't find douban ID by IMDb ID: " + imdbId);
        }
    }

    private void saveIdRelation(long dbId, String imdbId) {
        IdRelationEntity entity = new IdRelationEntity();
        entity.setImdbId(imdbId);
        entity.setDbId(dbId);
        relationRepository.save(entity);
    }

    @Override
    public SingleResult<Map<Long, LocalDate>> collectUserSubjects(long userId, LocalDate since, MarkEnum mark) throws NotFoundException {
        try {
            return SingleResult.of(doubanSite.collectUserSubjects(userId, since, CatalogEnum.MOVIE, mark));
        } catch (NotFoundException e) {
            throw new NotFoundException(errorMsg(doubanSite, "douban user subjects", userId, e));
        }
    }

    @Override
    public SingleResult<BaseImdbTitle> imdbTitle(String imdbId) throws NotFoundException {
        Objects.requireNonNull(imdbId);
        try {
            return SingleResult.of(imdbSite.title(imdbId));
        } catch (NotFoundException e) {
            throw new NotFoundException(errorMsg(imdbSite, IMDB_TITLE, imdbId, e));
        }
    }

    @Override
    public SingleResult<List<String[]>> episodes(String seriesId) throws NotFoundException {
        Objects.requireNonNull(seriesId);
        try {
            return SingleResult.of(imdbSite.episodes(seriesId));
        } catch (NotFoundException e) {
            throw new NotFoundException(errorMsg(imdbSite, "episodes", seriesId, e));
        }
    }

    private String errorMsg(BaseSite site, String resource, Object resourceId, NotFoundException cause) {
        return String.format("Can't get %s from %s, id: %s, reason: %s.", resource, site.getName(), resourceId, cause.getReasonPhrase());
    }

    @Autowired
    public void setRelationRepository(IdRelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }
}
