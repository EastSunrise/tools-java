package wsg.tools.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsg.tools.boot.config.VideoConfig;
import wsg.tools.boot.dao.mapper.SubjectMapper;
import wsg.tools.boot.entity.base.Result;
import wsg.tools.boot.entity.subject.dto.SubjectDto;
import wsg.tools.boot.entity.subject.enums.ArchivedEnum;
import wsg.tools.boot.entity.subject.enums.SubtypeEnum;
import wsg.tools.boot.entity.subject.query.QuerySubject;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
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

    private VideoConfig videoConfig;

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
    public Result updateInfo(long id) {
        try {
            if (updateById(videoConfig.subjectDto(id))) {
                return Result.success();
            }
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                SubjectDto subjectDto = getById(id);
                if (subjectDto.getImdbId() != null) {
                    try {
                        subjectDto = videoConfig.subjectDto(subjectDto.getImdbId());
                        subjectDto.setId(id);
                        if (updateById(subjectDto)) {
                            return Result.success();
                        }
                    } catch (IOException | URISyntaxException ex) {
                        log.error(e.getMessage());
                        return Result.fail(ex.getMessage());
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
            return Result.fail(e.getMessage());
        }
        log.error("Failed to update info of subject {}", id);
        return Result.fail("Failed to update info: %d", id);
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
        if (!Desktop.isDesktopSupported()) {
            return Result.fail("Not supported is Desktop.");
        }
        try {
            Desktop desktop = Desktop.getDesktop();
            File file;
            if (SubtypeEnum.MOVIE.equals(subject.getSubtype())) {
                file = new File(subject.getLocation());
            } else {
                file = Objects.requireNonNull(new File(subject.getLocation()).listFiles())[0];
            }
            desktop.open(file);
        } catch (NullPointerException | IOException e) {
            log.error(e.getMessage());
            return Result.fail(e.getMessage());
        }
        return Result.success();
    }

    @Autowired
    public void setVideoConfig(VideoConfig videoConfig) {
        this.videoConfig = videoConfig;
    }
}
