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
import wsg.tools.boot.pojo.enums.MarkEnum;
import wsg.tools.boot.pojo.enums.SubtypeEnum;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.util.SystemUtils;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.site.DoubanSite;

import javax.annotation.Nullable;
import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private SubjectRepository subjectRepository;

    @Override
    public PageResult<SubjectDto> list(QuerySubjectDto querySubjectDto, Pageable pageable) {
        Specification<SubjectEntity> spec = (Specification<SubjectEntity>) (root, query, builder) -> {
            Predicate predicate = getPredicate(querySubjectDto, root, builder);
            if (querySubjectDto.getNullDuration()) {
                predicate = builder.and(predicate, root.get(SubjectEntity_.imdbId).isNull());
            }
            return predicate;
        };
        return PageResult.of(findAll(spec, pageable));
    }

    @Override
    public Result importDouban(long userId, LocalDate startDate) {
        if (startDate == null) {
            startDate = subjectRepository.findMaxTagDate();
            if (startDate == null) {
                startDate = DoubanSite.START_DATE;
            }
        }
        log.info("Start to import douban subjects of {} since {}", userId, startDate);
        try {
            return batchSaveOrUpdate(userSubjects(userId, startDate));
        } catch (HttpResponseException e) {
            log.error(e.getReasonPhrase());
            return Result.fail(e);
        }
    }

    @Override
    public Result importImdbIds(List<String> ids) {
        return batchSaveOrUpdate(ids.stream().map(id -> {
            SubjectDto subject = new SubjectDto();
            subject.setImdbId(id);
            return subject;
        }).collect(Collectors.toList()));
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

    private Result batchSaveOrUpdate(List<SubjectDto> subjects) {
        final int[] count = {0};
        subjects.forEach(source -> {
            if (source == null) {
                return;
            }
            SubjectDto subject = getSubjectInfo(source.getDbId(), source.getImdbId());
            if (subject != null) {
                BeanUtilExt.copyPropertiesExceptNull(source, subject);
            }
            updateOrInsert(source, () -> subjectRepository.findByDbIdAndImdbId(source.getDbId(), source.getImdbId()));
            count[0]++;
        });
        log.info("Finish batch, total: {}, success: {}, fail: {}", subjects.size(), count[0], subjects.size() - count[0]);
        return Result.batchResult(subjects.size(), count[0]);
    }

    private List<SubjectDto> userSubjects(long userId, LocalDate startDate) throws HttpResponseException {
        return videoConfig.getDoubanSite().collectUserMovies(userId, startDate)
                .stream().map(s -> {
                    SubjectDto subject = BeanUtilExt.convert(s, SubjectDto.class);
                    subject.setMark(MarkEnum.of(s.getRecord()));
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
    public void setSubjectRepository(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Autowired
    public void setVideoConfig(VideoConfig videoConfig) {
        this.videoConfig = videoConfig;
    }
}
