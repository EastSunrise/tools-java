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
import wsg.tools.boot.pojo.enums.TypeEnum;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.excel.reader.CellReader;
import wsg.tools.common.excel.writer.CellWriter;
import wsg.tools.common.excel.writer.HyperlinkCellWriter;
import wsg.tools.common.excel.writer.NumericCellWriter;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.MarkEnum;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
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
            LinkedHashMap<String, Class<?>> classes = new LinkedHashMap<>(4);
            classes.put("Const", String.class);
            List<String> ids = readCsv(file, classes).stream()
                    .map(map -> (String) map.get("Const")).filter(Objects::nonNull).collect(Collectors.toList());
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
        LinkedHashMap<String, CellWriter<SubjectDto, ?>> writers = new LinkedHashMap<>(16);
        writers.put("ID", SubjectDto::getId);
        writers.put("豆瓣ID", new HyperlinkCellWriter<SubjectDto, Long>() {
            @Override
            public Long getValue(SubjectDto subjectDto) {
                return subjectDto.getDbId();
            }

            @Override
            public String getAddress(SubjectDto subjectDto) {
                return subjectDto.getDbId() != null ? String.format("https://movie.douban.com/subject/%d/", subjectDto.getDbId()) : null;
            }
        });
        writers.put("IMDb ID", new HyperlinkCellWriter<SubjectDto, String>() {
            @Override
            public String getValue(SubjectDto subjectDto) {
                return subjectDto.getImdbId();
            }

            @Override
            public String getAddress(SubjectDto subjectDto) {
                return subjectDto.getImdbId() != null ? String.format("https://imdb.com/title/%s/", subjectDto.getImdbId()) : null;
            }
        });
        writers.put("类型", SubjectDto::getType);
        writers.put("名称", SubjectDto::getTitle);
        writers.put("英文名", SubjectDto::getText);
        writers.put("原名", SubjectDto::getOriginalTitle);
        writers.put("别名", SubjectDto::getTitleAka);
        writers.put("外文别名", SubjectDto::getTextAka);
        writers.put("年份", (NumericCellWriter<SubjectDto, Year>) SubjectDto::getYear);
        writers.put("时长", SubjectDto::getDurations);
        writers.put("语言", SubjectDto::getLanguages);
        writers.put("集数", SubjectDto::getEpisodesCount);
        writers.put("季数", SubjectDto::getSeasonsCount);
        writers.put("当前季", SubjectDto::getCurrentSeason);
        writers.put("剧集 ID", SubjectDto::getSeriesId);
        try {
            exportXlsx(response, all.getPage().getContent(), "电影列表", writers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @SuppressWarnings("unchecked")
    public void importEx(MultipartFile file) {
        try {
            LinkedHashMap<String, CellReader<?>> readers = new LinkedHashMap<>(16);
            readers.put("ID", new CellReader<>(Long.class));
            readers.put("豆瓣ID", new CellReader<>(Long.class));
            readers.put("IMDb ID", CellReader.STRING_CELL_READER);
            readers.put("类型", new CellReader<>(TypeEnum.class));
            readers.put("名称", CellReader.STRING_CELL_READER);
            readers.put("英文名", CellReader.STRING_CELL_READER);
            readers.put("原名", CellReader.STRING_CELL_READER);
            readers.put("别名", new CellReader<>(new TypeReference<List<String>>() {}));
            readers.put("外文别名", new CellReader<>(new TypeReference<List<String>>() {}));
            readers.put("年份", new CellReader<>(Year.class) {
                @Override
                protected Class<?> getMiddleType() {
                    return Integer.TYPE;
                }
            });
            readers.put("时长", new CellReader<>(new TypeReference<List<Duration>>() {}));
            readers.put("语言", new CellReader<>(new TypeReference<List<LanguageEnum>>() {}));
            readers.put("集数", new CellReader<>(Integer.class));
            readers.put("季数", new CellReader<>(Integer.class));
            readers.put("当前季", new CellReader<>(Integer.class));
            readers.put("剧集 ID", new CellReader<>(Long.class));
            List<SubjectDto> subjects = readXlsx(file, readers).stream()
                    .map(map -> {
                        SubjectDto subject = new SubjectDto();
                        subject.setId((Long) map.get("ID"));
                        subject.setDbId((Long) map.get("豆瓣ID"));
                        subject.setImdbId((String) map.get("IMDb ID"));
                        subject.setType((TypeEnum) map.get("类型"));
                        subject.setTitle((String) map.get("名称"));
                        subject.setText((String) map.get("英文名"));
                        subject.setOriginalTitle((String) map.get("原名"));
                        subject.setTitleAka((List<String>) map.get("别名"));
                        subject.setTextAka((List<String>) map.get("外文别名"));
                        subject.setYear((Year) map.get("年份"));
                        subject.setDurations((List<Duration>) map.get("时长"));
                        subject.setLanguages((List<LanguageEnum>) map.get("语言"));
                        subject.setEpisodesCount((Integer) map.get("集数"));
                        subject.setSeasonsCount((Integer) map.get("季数"));
                        subject.setCurrentSeason((Integer) map.get("当前季"));
                        subject.setSeriesId((Long) map.get("剧集 ID"));
                        return subject;
                    }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
