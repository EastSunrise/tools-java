package wsg.tools.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import wsg.tools.boot.pojo.base.PageResult;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.dto.QuerySubjectDto;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.TypeEnum;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.lang.MapBuilder;
import wsg.tools.internet.video.enums.MarkEnum;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * API of video subjects.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@RestController
@RequestMapping("/api/video")
public class SubjectController extends AbstractController {

    private SubjectService subjectService;

    @RequestMapping(value = "/subjects", method = RequestMethod.GET)
    public ResponseEntity<?> index(QuerySubjectDto querySubject, Pageable pageable, PagedResourcesAssembler<SubjectDto> assembler) {
        PageResult<SubjectDto> result = subjectService.list(querySubject, pageable);
        result.put("archives", ArchivedEnum.values());
        result.put("marks", MarkEnum.values());
        result.put("types", TypeEnum.values());
        return result.toResponse(assembler);
    }

    @RequestMapping(value = "/douban", method = RequestMethod.POST)
    public ResponseEntity<?> updateDouban(long user, LocalDate since) {
        return subjectService.importDouban(user, since).toResponse();
    }

    @RequestMapping(value = "/imdb", method = RequestMethod.POST)
    public ResponseEntity<?> importImdb(MultipartFile file) {
        try {
            List<String> ids = readCsv(file, record -> record.get("Const"),
                    "Position", "Const", "Created", "Modified", "Description", "Title", "URL", "Title Type", "IMDb Rating",
                    "Runtime (mins)", "Year", "Genres", "Num Votes", "Release Date", "Directors", "Your Rating", "Date Rated")
                    .stream().filter(Objects::nonNull).collect(Collectors.toList());
            return subjectService.importImdbIds(ids).toResponse();
        } catch (IOException | IllegalArgumentException e) {
            return Result.fail(e).toResponse();
        }
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public ResponseEntity<?> play(long id) {
        return subjectService.play(id).toResponse();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<?> updateById(SubjectDto subject) {
        subjectService.updateById(subject);
        return Result.success().toResponse();
    }

    @RequestMapping(value = "/export")
    public void export(HttpServletResponse response, QuerySubjectDto querySubjectDto) {
        PageResult<SubjectDto> all = subjectService.list(querySubjectDto, null);
        try {
            exportCsv(response, all.getPage().getContent(), "电影列表",
                    MapBuilder.builder(new LinkedHashMap<String, Function<SubjectDto, ?>>())
                            .put("ID", SubjectDto::getId)
                            .put("豆瓣ID", SubjectDto::getDbId)
                            .put("IMDb ID", SubjectDto::getImdbId)
                            .put("豆瓣 Link", subjectDto -> {
                                if (subjectDto.getDbId() != null) {
                                    return String.format("https://movie.douban.com/subject/%d/", subjectDto.getDbId());
                                }
                                return null;
                            })
                            .put("IMDb Link", subjectDto -> {
                                if (subjectDto.getImdbId() != null) {
                                    return String.format("https://imdb.com/title/%s/", subjectDto.getImdbId());
                                }
                                return null;
                            })
                            .put("类型", SubjectDto::getType)
                            .put("名称", SubjectDto::getTitle)
                            .put("英文名", SubjectDto::getText)
                            .put("原名", SubjectDto::getOriginalTitle)
                            .put("别名", SubjectDto::getTitleAka)
                            .put("外文别名", SubjectDto::getTextAka)
                            .put("年份", SubjectDto::getYear)
                            .put("时长", SubjectDto::getDurations)
                            .put("语言", SubjectDto::getLanguages)
                            .put("集数", SubjectDto::getEpisodesCount)
                            .put("季数", SubjectDto::getSeasonsCount)
                            .put("当前季", SubjectDto::getCurrentSeason)
                            .put("剧集 ID", SubjectDto::getSeriesId)
                            .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
