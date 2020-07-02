package wsg.tools.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.BeanUtilExt;
import wsg.tools.boot.dao.api.VideoConfig;
import wsg.tools.boot.dao.mapper.SubjectMapper;
import wsg.tools.boot.entity.base.dto.GenericResult;
import wsg.tools.boot.entity.base.dto.Result;
import wsg.tools.boot.entity.subject.dto.SubjectDto;
import wsg.tools.boot.entity.subject.enums.ArchivedEnum;
import wsg.tools.boot.entity.subject.enums.StatusEnum;
import wsg.tools.boot.entity.subject.enums.SubtypeEnum;
import wsg.tools.boot.entity.subject.query.QuerySubject;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.util.SystemUtils;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.site.DoubanSite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashSet;
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
public class SubjectServiceImpl extends BaseServiceImpl<SubjectMapper, SubjectDto> implements SubjectService {

    private VideoConfig videoConfig;

    @Override
    public Page<SubjectDto> list(QuerySubject querySubject) {
        QueryWrapper<SubjectDto> wrapper = Wrappers.query();
        if (querySubject.isNullDuration()) {
            int year = LocalDate.now().getYear();
            wrapper.and(w -> w.isNull("durations").le("year", year));
        }
        if (querySubject.isBadSeason()) {
            wrapper.and(w -> w.eq("subtype", SubtypeEnum.SERIES)
                    .and(w1 -> w1.or(w2 -> w2.isNull("current_season"))
                            .or(w2 -> w2.isNull("episodes_count"))
                            .or(w2 -> w2.isNull("seasons_count"))));
        }
        if (querySubject.isNullImdb()) {
            wrapper.isNull("imdb_id");
        }
        invokeWrapper(querySubject.getStatus(), status -> wrapper.eq("status", status));
        invokeWrapper(querySubject.getArchived(), archived -> wrapper.eq("archived", archived));
        invokeWrapper(querySubject.getSubtype(), subtype -> wrapper.eq("subtype", subtype));
        return page(querySubject.getPage(), wrapper);
    }

    @Override
    public Result updateInfo(long id) {
        SubjectDto subject = getById(id);
        if (subject == null) {
            return Result.fail("Not exist does the subject %d", id);
        }
        subject = getSubjectInfo(subject.getDbId(), subject.getImdbId());
        if (subject == null) {
            log.warn("Can't obtain info of subject {}", id);
            return Result.fail("Can't obtain info of subject %d", id);
        }
        subject.setId(id);
        if (!saveOrUpdate(subject)) {
            log.error("Failed to update info of subject {}", id);
            return Result.fail("Failed to update info: %d", id);
        }
        return Result.success();
    }

    @Override
    public Result play(long id) {
        SubjectDto subject = getById(id);
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
    public GenericResult<Integer> collectSubjects(long userId, LocalDate startDate) {
        log.info("Start to collect subjects of {} since {}", userId, startDate);
        if (startDate == null) {
            QueryWrapper<SubjectDto> wrapper = new QueryWrapper<SubjectDto>().select("MAX(tag_date) AS tag_date");
            SubjectDto subjectDto = getOne(wrapper);
            if (subjectDto == null || subjectDto.getTagDate() == null) {
                startDate = DoubanSite.START_DATE;
            } else {
                startDate = subjectDto.getTagDate();
            }
        }
        List<SubjectDto> subjects = userSubjects(userId, startDate);
        if (subjects == null) {
            log.error("Failed to obtains subjects of user {}", userId);
            return new GenericResult<>("Failed to obtains subjects of user " + userId);
        }

        final int[] count = {0};
        subjects = subjects.stream().peek(s -> {
            SubjectDto subjectDto = getSubjectInfo(s.getDbId(), null);
            if (subjectDto != null) {
                try {
                    BeanUtilExt.copyPropertiesExceptNull(s, subjectDto);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            } else {
                count[0]++;
            }
        }).collect(Collectors.toList());

        if (subjects.size() != 0) {
            for (SubjectDto subject : subjects) {
                UpdateWrapper<SubjectDto> wrapper = Wrappers.update();
                wrapper.eq("db_id", subject.getDbId());
                if (!saveOrUpdate(subject, wrapper)) {
                    log.error("Failed to update subject {}", subject.getDbId());
                }
            }
        }

        log.info("Finish to collect subjects of {}, total: {}, not found: {}", userId, subjects.size(), count[0]);
        return new GenericResult<>(subjects.size());
    }

    private List<SubjectDto> userSubjects(long userId, LocalDate startDate) {
        List<Subject> subjects;
        try {
            subjects = videoConfig.getDoubanSite().collectUserMovies(userId, startDate);
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
            return null;
        }
        return subjects.stream().map(s -> {
            SubjectDto subjectDto = new SubjectDto();
            try {
                BeanUtilExt.copyPropertiesExceptNull(subjectDto, s);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.error(e.getMessage());
            }
            subjectDto.setStatus(StatusEnum.of(s.getRecord()));
            return subjectDto;
        }).collect(Collectors.toList());
    }

    private SubjectDto getSubjectInfo(Long dbId, String imdbId) {
        SubjectDto subjectDto = new SubjectDto();
        if (dbId != null) {
            try {
                Subject movieSubject = videoConfig.getDoubanSite().movieSubject(dbId);
                BeanUtilExt.copyPropertiesExceptNull(subjectDto, movieSubject);
                if (movieSubject.getSubtype() != null) {
                    subjectDto.setSubtype(SubtypeEnum.of(movieSubject.getSubtype()));
                }
            } catch (IOException | URISyntaxException | IllegalAccessException | InvocationTargetException e) {
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
                        subjectDto.setDurations(new HashSet<>());
                    }
                    subjectDto.getDurations().add(omSubject.getRuntime());
                }
            } catch (IOException | URISyntaxException | IllegalAccessException | InvocationTargetException e) {
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
