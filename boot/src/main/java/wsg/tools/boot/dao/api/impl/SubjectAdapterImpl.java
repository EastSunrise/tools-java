package wsg.tools.boot.dao.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import wsg.tools.boot.dao.api.intf.SubjectAdapter;
import wsg.tools.boot.dao.jpa.mapper.IdRelationRepository;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.entity.IdRelationEntity;
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
    public GenericResult<BaseDoubanSubject> doubanSubject(long dbId) {
        try {
            BaseDoubanSubject subject = doubanSite.subject(dbId);
            if (subject.getImdbId() != null) {
                saveIdRelation(dbId, subject.getImdbId());
            }
            return GenericResult.of(subject);
        } catch (NotFoundException e) {
            return new GenericResult<>(errorMsg(doubanSite, DOUBAN_SUBJECT, dbId, e));
        }
    }

    @Override
    public GenericResult<Long> getDbIdByImdbId(String imdbId) {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isPresent()) {
            return GenericResult.of(optional.get().getDbId());
        }
        Long dbId;
        try {
            dbId = doubanSite.getDbIdByImdbId(imdbId);
        } catch (LoginException e) {
            return new GenericResult<>("Can't find douban ID by IMDb ID without logging in.");
        }
        if (dbId != null) {
            saveIdRelation(dbId, imdbId);
            return GenericResult.of(dbId);
        } else {
            return new GenericResult<>("Can't find douban ID by IMDb ID: %s.", imdbId);
        }
    }

    private void saveIdRelation(long dbId, String imdbId) {
        IdRelationEntity entity = new IdRelationEntity();
        entity.setImdbId(imdbId);
        entity.setDbId(dbId);
        relationRepository.save(entity);
    }

    @Override
    public GenericResult<Map<Long, LocalDate>> collectUserSubjects(long userId, LocalDate since, MarkEnum mark) {
        try {
            return GenericResult.of(doubanSite.collectUserSubjects(userId, since, CatalogEnum.MOVIE, mark));
        } catch (NotFoundException e) {
            return new GenericResult<>(errorMsg(doubanSite, "douban user subjects", userId, e));
        }
    }

    @Override
    public GenericResult<BaseImdbTitle> imdbTitle(String imdbId) {
        Objects.requireNonNull(imdbId);
        try {
            return GenericResult.of(imdbSite.title(imdbId));
        } catch (NotFoundException e) {
            return new GenericResult<>(errorMsg(imdbSite, IMDB_TITLE, imdbId, e));
        }
    }

    @Override
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

    @Autowired
    public void setRelationRepository(IdRelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }
}
