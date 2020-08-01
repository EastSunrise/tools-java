package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import wsg.tools.boot.dao.api.VideoConfig;
import wsg.tools.boot.dao.jpa.mapper.SubjectRepository;
import wsg.tools.boot.dao.jpa.mapper.UserRecordRepository;
import wsg.tools.boot.pojo.base.PageResult;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.dto.QuerySubjectDto;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.pojo.entity.SubjectEntity_;
import wsg.tools.boot.pojo.entity.UserRecordEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.DoubanSite;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    private UserRecordRepository userRecordRepository;

    @Override
    public Result importDouban(long userId, LocalDate since) {
        if (since == null) {
            since = userRecordRepository.findMaxMarkDate();
            if (since == null) {
                since = DoubanSite.DOUBAN_START_DATE;
            }
        }
        log.info("Start to import douban subjects of {} since {}", userId, since);
        int added = 0, exists = 0, notFound = 0;
        for (MarkEnum mark : MarkEnum.values()) {
            Map<Long, LocalDate> map;
            try {
                map = videoConfig.doubanSite().collectUserSubjects(userId, since, CatalogEnum.MOVIE, mark);
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
                continue;
            }
            for (Map.Entry<Long, LocalDate> entry : map.entrySet()) {
                if (subjectRepository.findByDbId(entry.getKey()).isEmpty()) {
                    SubjectEntity entity = videoConfig.getSubjectEntity(entry.getKey(), null);
                    if (entity != null) {
                        subjectRepository.insert(entity);
                        added++;
                        continue;
                    }
                    notFound++;
                    continue;
                }
                exists++;

                UserRecordEntity entity = new UserRecordEntity();
                entity.setMark(mark);
                entity.setMarkDate(entry.getValue());
                entity.setSubjectId(entry.getKey());
                entity.setUserId(userId);
                userRecordRepository.updateOrInsert(entity,
                        () -> userRecordRepository.findBySubjectIdAndUserId(entry.getKey(), userId));
            }
        }
        Result result = Result.success();
        result.put("added", added);
        result.put("exists", exists);
        result.put("not found", notFound);
        result.put("total", added + exists + notFound);
        return result;
    }

    @Override
    public Result importImdbIds(List<String> ids) {
        log.info("Start to import imdb watchlist.");
        int added = 0, exists = 0, notFound = 0;
        for (String id : ids) {
            if (subjectRepository.findByImdbId(id).isEmpty()) {
                SubjectEntity entity = videoConfig.getSubjectEntity(null, id);
                if (entity != null) {
                    subjectRepository.insert(entity);
                    added++;
                    continue;
                }
                notFound++;
                continue;
            }
            exists++;
        }
        Result result = Result.success();
        result.put("added", added);
        result.put("exists", exists);
        result.put("not found", notFound);
        result.put("total", ids.size());
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

    @Autowired
    public void setUserRecordRepository(UserRecordRepository userRecordRepository) {
        this.userRecordRepository = userRecordRepository;
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
