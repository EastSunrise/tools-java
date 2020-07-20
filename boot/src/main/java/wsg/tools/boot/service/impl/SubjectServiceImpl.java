package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.BeanUtilExt;
import wsg.tools.boot.dao.api.VideoConfig;
import wsg.tools.boot.dao.jpa.mapper.SubjectRepository;
import wsg.tools.boot.pojo.base.PageResult;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.dto.QuerySubjectDto;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.pojo.entity.SubjectEntity_;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.util.SystemUtils;
import wsg.tools.internet.video.site.DoubanSite;

import javax.annotation.Nullable;
import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implement of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Slf4j
@Service
public class SubjectServiceImpl extends BaseServiceImpl<SubjectDto, SubjectEntity, Long> implements SubjectService {

    private VideoConfig videoConfig;
    private SubjectRepository subjectRepository;

    @Override
    public Result importDouban(long userId, LocalDate since) {
        if (since == null) {
            since = subjectRepository.findMaxMarkDate();
            if (since == null) {
                since = DoubanSite.START_DATE;
            }
        }
        log.info("Start to import douban subjects of {} since {}", userId, since);
        final int[] count = {0};
        try {
            List<SubjectEntity> entities = videoConfig.collectUserMovies(userId, since);
            entities.forEach(source -> {
                SubjectEntity entity = getSubjectInfo(source.getDbId(), null);
                if (entity != null) {
                    BeanUtilExt.copyPropertiesExceptNull(entity, source);
                } else {
                    entity = source;
                }
                SubjectEntity finalEntity = entity;
                if (subjectRepository.insertIgnore(entity, () -> subjectRepository.findByDbId(finalEntity.getDbId())) != null) {
                    count[0]++;
                }
            });
            Result result = Result.success();
            result.put("total", entities.size());
            result.put("inserted", count[0]);
            result.put("exists", entities.size() - count[0]);
            return result;
        } catch (HttpResponseException e) {
            log.error(e.getReasonPhrase());
            return Result.fail(e);
        }
    }

    @Override
    public Result importImdbIds(List<String> ids) {
        log.info("Start to import imdb watchlist.");
        final int[] count = {0};
        ids.forEach(id -> {
            SubjectEntity entity = getSubjectInfo(null, id);
            if (entity != null) {
                if (subjectRepository.insertIgnore(entity, () -> subjectRepository.findByImdbId(entity.getImdbId())) != null) {
                    count[0]++;
                }
            }
        });
        Result result = Result.success();
        result.put("total", ids.size());
        result.put("inserted", count[0]);
        result.put("others", ids.size() - count[0]);
        return result;
    }

    @Override
    public PageResult<SubjectDto> list(QuerySubjectDto querySubjectDto, Pageable pageable) {
        Specification<SubjectEntity> spec = (Specification<SubjectEntity>) (root, query, builder) -> {
            Predicate predicate = getPredicate(querySubjectDto, root, builder);
            if (querySubjectDto.getIncomplete()) {
                Predicate or = builder.or(root.get(SubjectEntity_.imdbId).isNull(), root.get(SubjectEntity_.dbId).isNull());
                return builder.and(predicate, or);
            }
            return predicate;
        };
        if (pageable == null) {
            return PageResult.of(findAll(spec));
        }
        return PageResult.of(findAll(spec, pageable));
    }

    @Override
    public Result play(long id) {
        Optional<SubjectDto> optional = findById(id);
        if (optional.isEmpty()) {
            return Result.fail("Not exist does the subject %d", id);
        }
        SubjectDto subject = optional.get();
        if (!ArchivedEnum.PLAYABLE.equals(subject.getArchived())) {
            return Result.fail("Not archived yet.");
        }

        File file = null;
        try {
            SystemUtils.openFile(file);
        } catch (IOException e) {
            log.error(e.getMessage());
            return Result.fail(e);
        }
        return Result.success();
    }

    @Nullable
    private SubjectEntity getSubjectInfo(Long dbId, String imdbId) {
        SubjectEntity subject = null;
        if (dbId != null) {
            try {
                subject = videoConfig.getDouban(dbId);
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
        }
        if (subject != null && subject.getImdbId() != null) {
            imdbId = subject.getImdbId();
        }
        if (imdbId != null) {
            try {
                SubjectEntity entity = videoConfig.getImdb(imdbId);
                if (subject != null) {
                    BeanUtilExt.copyPropertiesExceptNull(subject, entity);
                } else {
                    subject = entity;
                }
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
        }
        return subject;
    }

    @Autowired
    public void setSubjectRepository(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Autowired
    public void setVideoConfig(VideoConfig videoConfig) {
        this.videoConfig = videoConfig;
    }
}
