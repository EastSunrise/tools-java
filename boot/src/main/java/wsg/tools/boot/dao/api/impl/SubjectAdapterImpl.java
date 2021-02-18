package wsg.tools.boot.dao.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wsg.tools.boot.dao.api.intf.SubjectAdapter;
import wsg.tools.boot.dao.jpa.mapper.IdRelationRepository;
import wsg.tools.boot.pojo.entity.subject.IdRelationEntity;
import wsg.tools.boot.pojo.result.SingleResult;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.exception.LoginException;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.douban.BaseDoubanSubject;
import wsg.tools.internet.video.site.douban.DoubanSite;
import wsg.tools.internet.video.site.imdb.ImdbCnSite;
import wsg.tools.internet.video.site.imdb.ImdbRepository;
import wsg.tools.internet.video.site.imdb.ImdbTitle;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Kingen
 * @since 2020/6/26
 */
@Slf4j
@Component
public class SubjectAdapterImpl implements SubjectAdapter, DisposableBean {

    private static final String DOUBAN_SUBJECT = "douban subject";
    private static final String IMDB_TITLE = "imdb title";

    private final ImdbRepository<ImdbTitle> imdbRepository = ImdbCnSite.getInstance();
    private final DoubanSite doubanSite = DoubanSite.getInstance();

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
    public SingleResult<Long> getDbIdByImdbId(String imdbId) throws NotFoundException {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isPresent()) {
            return SingleResult.of(optional.get().getDbId());
        }
        if (doubanSite.user() != null) {
            Long dbIdByImdbId;
            try {
                dbIdByImdbId = doubanSite.getDbIdByImdbId(imdbId);
            } catch (LoginException e) {
                throw AssertUtils.runtimeException(e);
            }
            if (dbIdByImdbId != null) {
                return SingleResult.of(saveIdRelation(dbIdByImdbId, imdbId));
            }
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
    public SingleResult<Map<Long, LocalDate>> collectUserSubjects(long userId, LocalDate since, MarkEnum mark) throws NotFoundException {
        try {
            return SingleResult.of(doubanSite.collectUserSubjects(userId, since, CatalogEnum.MOVIE, mark));
        } catch (NotFoundException e) {
            throw new NotFoundException(errorMsg(doubanSite, "douban user subjects", userId, e));
        }
    }

    @Override
    public SingleResult<ImdbTitle> imdbTitle(String imdbId) throws NotFoundException {
        Objects.requireNonNull(imdbId);
        try {
            return SingleResult.of(imdbRepository.getItemById(imdbId));
        } catch (NotFoundException e) {
            throw new NotFoundException(errorMsg((BaseSite) imdbRepository, IMDB_TITLE, imdbId, e));
        }
    }

    private String errorMsg(BaseSite site, String resource, Object resourceId, NotFoundException cause) {
        return String.format("Can't get %s from %s, id: %s, reason: %s.", resource, site.getName(), resourceId, cause.getReasonPhrase());
    }

    @Autowired
    public void setRelationRepository(IdRelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }

    @Override
    public void destroy() throws Exception {
        doubanSite.close();

    }
}
