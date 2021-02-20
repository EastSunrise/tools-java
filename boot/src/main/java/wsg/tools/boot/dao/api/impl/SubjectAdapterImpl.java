package wsg.tools.boot.dao.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.dao.api.intf.SubjectAdapter;
import wsg.tools.boot.dao.jpa.mapper.IdRelationRepository;
import wsg.tools.boot.pojo.entity.subject.IdRelationEntity;
import wsg.tools.boot.pojo.result.SingleResult;
import wsg.tools.common.util.function.throwable.ThrowableFunction;
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

    private final ImdbRepository<ImdbTitle> imdbRepository = ImdbCnSite.getInstance();
    private final DoubanSite doubanSite = DoubanSite.getInstance();

    private final IdRelationRepository relationRepository;

    @Autowired
    public SubjectAdapterImpl(IdRelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }

    @Override
    public SingleResult<BaseDoubanSubject> doubanSubject(long dbId) throws HttpResponseException, NotFoundException {
        BaseDoubanSubject subject = handleException(dbId, doubanSite::subject);
        if (subject.getImdbId() != null) {
            saveIdRelation(dbId, subject.getImdbId());
        }
        return SingleResult.of(subject);
    }

    @Override
    public SingleResult<Long> getDbIdByImdbId(String imdbId) throws HttpResponseException, NotFoundException {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isPresent()) {
            return SingleResult.of(optional.get().getDbId());
        }
        Long dbIdByImdbId = doubanSite.getDbIdByImdbId(imdbId);
        if (dbIdByImdbId != null) {
            return SingleResult.of(saveIdRelation(dbIdByImdbId, imdbId));
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
    public SingleResult<Map<Long, LocalDate>> collectUserSubjects(long userId, LocalDate since, MarkEnum mark) throws HttpResponseException, NotFoundException {
        return SingleResult.of(handleException(userId, user -> doubanSite.collectUserSubjects(user, since, CatalogEnum.MOVIE, mark)));
    }

    @Override
    public SingleResult<ImdbTitle> imdbTitle(String imdbId) throws HttpResponseException, NotFoundException {
        return SingleResult.of(handleException(Objects.requireNonNull(imdbId), imdbRepository::getItemById));
    }

    private <T, R> R handleException(T t, ThrowableFunction<T, R, HttpResponseException> function) throws NotFoundException, HttpResponseException {
        try {
            return function.apply(t);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException(e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public void destroy() throws Exception {
        doubanSite.close();
    }
}
