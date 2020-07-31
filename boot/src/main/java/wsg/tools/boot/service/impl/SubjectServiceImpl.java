package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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
public class SubjectServiceImpl extends BaseServiceImpl implements SubjectService {

    private VideoConfig videoConfig;
    private SubjectRepository subjectRepository;

    @Override
    public Result importDouban(long userId, LocalDate since) {
        if (since == null) {
            since = subjectRepository.findMaxMarkDate();
            if (since == null) {
                since = DoubanSite.DOUBAN_START_DATE;
            }
        }
        log.info("Start to import douban subjects of {} since {}", userId, since);
        int count = 0;
        try {
            List<SubjectEntity> entities = videoConfig.collectUserSubjects(userId, since);
            for (SubjectEntity entity : entities) {
                if (subjectRepository.findByDbId(entity.getDbId()).isEmpty()) {
                    subjectRepository.insert(entity);
                    count++;
                }
            }
            Result result = Result.success();
            result.put("total", entities.size());
            result.put("inserted", count);
            result.put("exists", entities.size() - count);
            return result;
        } catch (HttpResponseException e) {
            log.error(e.getReasonPhrase());
            return Result.fail(e);
        }
    }

    @Override
    public Result importImdbIds(List<String> ids) {
        log.info("Start to import imdb watchlist.");
        int count = 0;
        for (String id : ids) {
            SubjectEntity entity = null;
            try {
                entity = videoConfig.getImdbSubject(id);
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
            if (entity != null && subjectRepository.findByImdbId(entity.getImdbId()).isEmpty()) {
                subjectRepository.insert(entity);
                count++;
            }
        }
        Result result = Result.success();
        result.put("total", ids.size());
        result.put("inserted", count);
        result.put("others", ids.size() - count);
        return result;
    }

    @Override
    public Result batchUpdate(List<SubjectDto> subjects) {
        log.info("Start to update list of subjects.");
        int count = 0;
        for (SubjectDto subject : subjects) {
            SubjectEntity entity = videoConfig.getSubjectEntity(subject.getDbId(), subject.getImdbId());
            if (subject.getId() != null && entity != null) {
                entity.setId(subject.getId());
                if (subjectRepository.updateById(entity) != null) {
                    count++;
                }
            }
        }
        Result result = Result.success();
        result.put("total", subjects.size());
        result.put("updated", count);
        result.put("others", subjects.size() - count);
        return result;
    }

    @Override
    public PageResult<SubjectDto> list(QuerySubjectDto querySubjectDto, Pageable pageable) {
        Specification<SubjectEntity> spec = (Specification<SubjectEntity>) (root, query, builder) -> {
            Predicate predicate = getPredicate(querySubjectDto, root, builder, SubjectEntity.class);
            if (querySubjectDto.getIncomplete()) {
                Predicate or = builder.or(root.get(SubjectEntity_.imdbId).isNull(), root.get(SubjectEntity_.dbId).isNull());
                return builder.and(predicate, or);
            }
            return predicate;
        };
        if (pageable == null) {
            return PageResult.of(convertEntities(subjectRepository.findAll(spec), SubjectDto.class));
        }
        return PageResult.of(subjectRepository.findAll(spec, pageable).map(entity -> convertEntity(entity, SubjectDto.class)));
    }

    @Override
    public Result play(long id) {
        Optional<SubjectDto> optional = subjectRepository.findById(id).map(entity -> convertEntity(entity, SubjectDto.class));
        if (optional.isEmpty()) {
            return Result.fail("Not exist does the subject %d", id);
        }
        SubjectDto subject = optional.get();
        if (!ArchivedEnum.PLAYABLE.equals(subject.getArchived())) {
            return Result.fail("Not archived yet.");
        }

        // todo play
        File file = null;
        try {
            SystemUtils.openFile(file);
        } catch (IOException e) {
            log.error(e.getMessage());
            return Result.fail(e);
        }
        return Result.success();
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
