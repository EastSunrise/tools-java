package wsg.tools.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsg.tools.boot.dao.api.VideoSite;
import wsg.tools.boot.dao.mapper.SubjectMapper;
import wsg.tools.boot.entity.base.dto.GenericResult;
import wsg.tools.boot.entity.base.dto.Result;
import wsg.tools.boot.entity.subject.dto.SubjectDto;
import wsg.tools.boot.entity.subject.enums.ArchivedEnum;
import wsg.tools.boot.entity.subject.enums.SubtypeEnum;
import wsg.tools.boot.entity.subject.query.QuerySubject;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.util.SystemUtils;
import wsg.tools.internet.video.site.DoubanSite;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Implement of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Slf4j
@Service
public class SubjectServiceImpl extends BaseServiceImpl<SubjectMapper, SubjectDto> implements SubjectService {

    private VideoSite videoSite;

    @Override
    public Page<SubjectDto> list(QuerySubject querySubject) {
        QueryWrapper<SubjectDto> wrapper = new QueryWrapper<>();
        if (querySubject.isNullDuration()) {
            int year = LocalDate.now().getYear();
            wrapper.and(w -> w.isNull("durations").le("year", year));
        }
        if (querySubject.isBadSeason()) {
            wrapper.and(w -> w.eq("subtype", SubtypeEnum.SERIES)
                    .and(w1 -> w1.isNull("current_season")
                            .isNull("episodes_count")
                            .isNull("seasons_count")));
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
    public Result saveOrUpdateInfo(long id) {
        GenericResult<SubjectDto> result = getSubjectInfo(id);
        if (!result.isSuccess()) {
            return result;
        }
        if (!saveOrUpdate(result.getRecord())) {
            log.error("Failed to update info of subject {}", id);
            return Result.fail("Failed to update info: %d", id);
        }
        return Result.success();
    }

    @Override
    public Result play(long id) {
        SubjectDto subject = getById(id);
        if (subject == null) {
            return Result.fail("Not exist does the subject.");
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
            QueryWrapper<SubjectDto> wrapper = new QueryWrapper<SubjectDto>().select("MAX(tag_date)");
            SubjectDto subjectDto = getOne(wrapper);
            if (subjectDto == null || subjectDto.getTagDate() == null) {
                startDate = DoubanSite.START_DATE;
            } else {
                startDate = subjectDto.getTagDate();
            }
        }
        List<SubjectDto> subjects;
        try {
            subjects = videoSite.userSubjects(userId, startDate);
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
            return new GenericResult<>(e);
        }

        int successCount = 0;
        for (SubjectDto simple : subjects) {
            GenericResult<SubjectDto> result = getSubjectInfo(simple.getId());
            if (result.isSuccess()) {
                BeanUtils.copyProperties(result.getRecord(), simple, "status", "tagDate");
            }
            simple.setArchived(ArchivedEnum.ADDED);
            if (saveOrUpdate(simple)) {
                successCount++;
            } else {
                log.error("Failed to update subject of {}", simple.getTitle());
            }
        }

        log.info("Finish to collect subjects of {}", userId);
        int errorCount = subjects.size() - successCount;
        if (errorCount > 0) {
            return new GenericResult<>(String.format("Not all succeed, fail: %d, success: %d", errorCount, successCount));
        }
        return new GenericResult<>(successCount);
    }

    private GenericResult<SubjectDto> getSubjectInfo(long id) {
        try {
            return new GenericResult<>(videoSite.subjectDto(id));
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                SubjectDto subjectDto = getById(id);
                if (subjectDto != null && subjectDto.getImdbId() != null) {
                    try {
                        SubjectDto subject = videoSite.subjectDto(subjectDto.getImdbId());
                        subject.setId(id);
                        return new GenericResult<>(subject);
                    } catch (IOException | URISyntaxException ex) {
                        log.error(e.getMessage());
                        return new GenericResult<>(ex);
                    }
                }
            }
            log.error("Not Found");
            return new GenericResult<>("Not Found");
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
            return new GenericResult<>(e);
        }
    }

    @Autowired
    public void setVideoConfig(VideoSite videoSite) {
        this.videoSite = videoSite;
    }
}
