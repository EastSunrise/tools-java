package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.util.BeanUtilExt;
import wsg.tools.boot.dao.api.VideoConfig;
import wsg.tools.boot.pojo.base.BatchResult;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.StatusEnum;
import wsg.tools.boot.pojo.enums.SubtypeEnum;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.util.SystemUtils;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.site.DoubanSite;

import javax.annotation.Nullable;
import javax.persistence.criteria.Path;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public BatchResult importDouban(long userId, LocalDate startDate) {
        if (startDate == null) {
            SubjectDto subject = findOne((Specification<SubjectEntity>) (root, criteriaQuery, criteriaBuilder) -> {
                Path<LocalDate> tagDate = root.get("tagDate");
                criteriaBuilder.greatest(tagDate);
                return criteriaQuery.getRestriction();
            });
            if (subject == null || subject.getTagDate() == null) {
                startDate = DoubanSite.START_DATE;
            } else {
                startDate = subject.getTagDate();
            }
        }
        log.info("Start to import douban subjects getInstance {} since {}", userId, startDate);
        List<SubjectDto> subjects = userSubjects(userId, startDate);
        if (subjects == null) {
            log.error("Failed to obtains subjects getInstance user {}", userId);
            return new BatchResult("Failed to obtains subjects getInstance user " + userId);
        }

        return batchSaveOrUpdate(subjects);
    }

    @Override
    public BatchResult importImdbIds(List<String> ids) {
        return batchSaveOrUpdate(ids.stream().map(id -> {
            SubjectDto subject = new SubjectDto();
            subject.setImdbId(id);
            return subject;
        }).collect(Collectors.toList()));
    }

    @Override
    public Result play(long id) {
        SubjectDto subject = findById(id);
        if (subject == null) {
            return Result.fail("Not exist does the subject %d", id);
        }
        if (!ArchivedEnum.PLAYABLE.equals(subject.getArchived())) {
            return Result.fail("Not archived yet.");
        }

        File file;
        if (SubtypeEnum.MOVIE.equals(subject.getSubtype())) {
            file = new File(subject.getLocation());
        } else {
            file = Objects.requireNonNull(new File(subject.getLocation()).listFiles())[0];
        }
        try {
            SystemUtils.openFile(file);
        } catch (IOException e) {
            log.error(e.getMessage());
            return Result.fail(e);
        }
        return Result.success();
    }

    @Override
    public void updateById(SubjectDto subject) {
        save(subject);
    }

    private BatchResult batchSaveOrUpdate(List<SubjectDto> subjects) {
        subjects = subjects.stream().peek(s -> {
            if (s == null) {
                return;
            }
            SubjectDto subjectDto = getSubjectInfo(s.getDbId(), s.getImdbId());
            if (subjectDto != null) {
                BeanUtilExt.copyPropertiesExceptNull(s, subjectDto);
            }
        }).collect(Collectors.toList());

        final int[] count = {0};
        if (subjects.size() != 0) {
            subjects.forEach(subject -> {
                if (subject == null) {
                    return;
                }
                count[0]++;
            });
        }
        log.info("Finish batch, total: {}, success: {}, fail: {}", subjects.size(), count[0], subjects.size() - count[0]);
        return new BatchResult(subjects.size(), count[0]);
    }

    @Nullable
    private List<SubjectDto> userSubjects(long userId, LocalDate startDate) {
        List<Subject> subjects;
        try {
            subjects = videoConfig.getDoubanSite().collectUserMovies(userId, startDate);
        } catch (HttpResponseException e) {
            log.error(e.getMessage());
            return null;
        }
        return subjects.stream().map(s -> {
            SubjectDto subject = new SubjectDto();
            BeanUtilExt.copyPropertiesExceptNull(subject, s);
            subject.setStatus(StatusEnum.of(s.getRecord()));
            return subject;
        }).collect(Collectors.toList());
    }

    @Nullable
    private SubjectDto getSubjectInfo(Long dbId, String imdbId) {
        SubjectDto subjectDto = new SubjectDto();
        if (dbId != null) {
            try {
                Subject movieSubject = videoConfig.getDoubanSite().movieSubject(dbId);
                BeanUtilExt.copyPropertiesExceptNull(subjectDto, movieSubject);
                if (movieSubject.getSubtype() != null) {
                    subjectDto.setSubtype(SubtypeEnum.of(movieSubject.getSubtype()));
                }
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
        }
        if (subjectDto.getImdbId() != null) {
            imdbId = subjectDto.getImdbId();
        }
        if (imdbId != null) {
            try {
                Subject omSubject = videoConfig.getOmdbSite().getSubjectById(imdbId);
                BeanUtilExt.copyPropertiesExceptNull(subjectDto, omSubject);
                if (omSubject.getSubtype() != null) {
                    subjectDto.setSubtype(SubtypeEnum.of(omSubject.getSubtype()));
                }
                if (omSubject.getRuntime() != null) {
                    if (subjectDto.getDurations() == null) {
                        subjectDto.setDurations(new LinkedList<>());
                    }
                    subjectDto.getDurations().add(omSubject.getRuntime());
                }
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
        }
        if (subjectDto.getDbId() == null && subjectDto.getImdbId() == null) {
            return null;
        }
        return subjectDto;
    }

    @Autowired
    public void setVideoConfig(VideoConfig videoConfig) {
        this.videoConfig = videoConfig;
    }
}
