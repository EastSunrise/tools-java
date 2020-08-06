package wsg.tools.boot.controller;

import lombok.extern.slf4j.Slf4j;
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
import wsg.tools.common.excel.reader.BaseCellToSetter;
import wsg.tools.common.excel.writer.BaseCellFromGetter;
import wsg.tools.internet.video.enums.MarkEnum;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API of video subjects.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Slf4j
@RestController
@RequestMapping("/api/video")
public class SubjectController extends AbstractController {

    private SubjectService subjectService;

    @RequestMapping(value = "/subjects", method = RequestMethod.GET)
    public ResponseEntity<?> subjects(QuerySubjectDto querySubject, Pageable pageable, PagedResourcesAssembler<SubjectDto> assembler) {
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
            LinkedHashMap<String, BaseCellToSetter<SubjectDto, ?>> readers = ExcelTemplate.<SubjectDto>builder()
                    .putSetter("Const", SubjectDto::setImdbId, String.class).getReaders();
            List<SubjectDto> subjects = readCsv(file, readers, SubjectDto::new, Constants.UTF_8);
            return subjectService.importImdbIds(subjects.stream().map(SubjectDto::getImdbId).collect(Collectors.toList())).toResponse();
        } catch (IOException | IllegalArgumentException e) {
            return Result.response(e);
        }
    }

    @RequestMapping(value = "/not", method = RequestMethod.GET)
    public void notFound(HttpServletResponse response) {
        LinkedHashMap<String, BaseCellFromGetter<Object, ?>> writers = ExcelTemplate.builder()
                .putGetter("ID", o -> o).getWriters();
        try {
            exportXlsx(response, subjectService.notFounds(), "Not Found", writers);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
