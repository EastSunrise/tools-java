package wsg.tools.boot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
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
import wsg.tools.boot.pojo.enums.VideoTypeEnum;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.excel.ExcelTemplate;
import wsg.tools.common.excel.reader.CellToSetter;
import wsg.tools.common.excel.writer.HyperlinkCellFromGetter;
import wsg.tools.common.excel.writer.NumericCellFromGetter;
import wsg.tools.internet.video.enums.MarkEnum;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
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

    private final ExcelTemplate<SubjectDto> subjectTemplate = ExcelTemplate.<SubjectDto>builder()
            .put("ID", SubjectDto::getId, SubjectDto::setId, Long.class)
            .putWriter("豆瓣ID", new HyperlinkCellFromGetter<SubjectDto, Long>() {
                @Override
                public Long getValue(SubjectDto subjectDto) {
                    return subjectDto.getDbId();
                }

                @Override
                public String getAddress(SubjectDto subjectDto) {
                    return subjectDto.getDbId() != null ? String.format("https://movie.douban.com/subject/%d/", subjectDto.getDbId()) : null;
                }
            }).putSetter("豆瓣ID", SubjectDto::setDbId, Long.class)
            .putWriter("IMDb ID", new HyperlinkCellFromGetter<SubjectDto, String>() {
                @Override
                public String getValue(SubjectDto subjectDto) {
                    return subjectDto.getImdbId();
                }

                @Override
                public String getAddress(SubjectDto subjectDto) {
                    return subjectDto.getImdbId() != null ? String.format("https://imdb.com/title/%s/", subjectDto.getImdbId()) : null;
                }
            }).putSetter("IMDb ID", SubjectDto::setImdbId, String.class)
            .put("类型", SubjectDto::getType, SubjectDto::setType, VideoTypeEnum.class)
            .put("名称", SubjectDto::getTitle, SubjectDto::setTitle, String.class)
            .put("英文名", SubjectDto::getText, SubjectDto::setText, String.class)
            .put("原名", SubjectDto::getOriginalTitle, SubjectDto::setOriginalTitle, String.class)
            .put("别名", SubjectDto::getTitleAka, SubjectDto::setTitleAka, new TypeReference<>() {})
            .put("外文别名", SubjectDto::getTextAka, SubjectDto::setTextAka, new TypeReference<>() {})
            .putWriter("年份", new NumericCellFromGetter<SubjectDto, Year>() {
                @Override
                public Year getValue(SubjectDto subjectDto) {
                    return subjectDto.getYear();
                }
            }).putSetter("年份", SubjectDto::setYear, Year.class)
            .put("时长", SubjectDto::getDurations, SubjectDto::setDurations, new TypeReference<>() {})
            .put("语言", SubjectDto::getLanguages, SubjectDto::setLanguages, new TypeReference<>() {})
            .put("集数", SubjectDto::getEpisodesCount, SubjectDto::setEpisodesCount, Integer.class)
            .put("季数", SubjectDto::getSeasonsCount, SubjectDto::setSeasonsCount, Integer.class)
            .put("当前季", SubjectDto::getCurrentSeason, SubjectDto::setCurrentSeason, Integer.class)
            .put("剧集 ID", SubjectDto::getSeriesId, SubjectDto::setSeriesId, Long.class);
    private SubjectService subjectService;

    @RequestMapping(value = "/subjects", method = RequestMethod.GET)
    public ResponseEntity<?> index(QuerySubjectDto querySubject, Pageable pageable, PagedResourcesAssembler<SubjectDto> assembler) {
        PageResult<SubjectDto> result = subjectService.list(querySubject, pageable);
        result.put("archives", ArchivedEnum.values());
        result.put("marks", MarkEnum.values());
        result.put("types", VideoTypeEnum.values());
        return result.toResponse(assembler);
    }

    @RequestMapping(value = "/douban", method = RequestMethod.POST)
    public ResponseEntity<?> updateDouban(long user, LocalDate since) {
        return subjectService.importDouban(user, since).toResponse();
    }

    @RequestMapping(value = "/imdb", method = RequestMethod.POST)
    public ResponseEntity<?> importImdb(MultipartFile file) {
        try {
            LinkedHashMap<String, CellToSetter<SubjectDto, ?>> readers = ExcelTemplate.<SubjectDto>builder()
                    .putSetter("Const", SubjectDto::setImdbId, String.class).getReaders();
            List<SubjectDto> subjects = readCsv(file, readers, SubjectDto::new, Constants.UTF_8);
            return subjectService.importImdbIds(subjects.stream().map(SubjectDto::getImdbId).collect(Collectors.toList())).toResponse();
        } catch (IOException | IllegalArgumentException e) {
            return Result.response(e);
        }
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public ResponseEntity<?> play(long id) {
        return subjectService.play(id).toResponse();
    }

    @RequestMapping(value = "/export")
    public ResponseEntity<?> export(HttpServletResponse response, QuerySubjectDto querySubjectDto) {
        PageResult<SubjectDto> all = subjectService.list(querySubjectDto, null);
        try {
            exportXlsx(response, all.getPage().getContent(), "电影列表", subjectTemplate.getWriters());
            return Result.response();
        } catch (IOException e) {
            return Result.response(e);
        }
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseEntity<?> importEx(MultipartFile file) {
        try {
            List<SubjectDto> subjects = readXlsx(file, subjectTemplate.getReaders(), SubjectDto::new);
            return subjectService.batchUpdate(subjects).toResponse();
        } catch (IOException e) {
            return Result.response(e);
        }
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
