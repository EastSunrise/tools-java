package wsg.tools.boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
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
import wsg.tools.boot.pojo.result.ImportResult;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.excel.ExcelTemplate;
import wsg.tools.internet.video.enums.MarkEnum;

import java.io.IOException;
import java.time.LocalDate;
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

    private static final ExcelTemplate<DefaultKeyValue<String, Long>> NOT_FOUND = ExcelTemplate.<DefaultKeyValue<String, Long>>builder()
            .put("IMDb", DefaultKeyValue::getKey, DefaultKeyValue::setKey, String.class)
            .put("Douban", DefaultKeyValue::getValue, DefaultKeyValue::setValue, Long.class);
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

    @RequestMapping(value = "/relation", method = RequestMethod.POST)
    public ResponseEntity<?> importRelations(MultipartFile file) {
        try {
            List<DefaultKeyValue<String, Long>> all = readXlsx(file, NOT_FOUND.getReaders(), DefaultKeyValue::new);
            List<DefaultKeyValue<String, Long>> keyValues = all.stream().filter(keyValue -> keyValue.getKey() != null).collect(Collectors.toList());
            ImportResult importResult = subjectService.importManually(keyValues);
            importResult.put("Wrong Rows", all.size() - keyValues.size());
            return importResult.toResponse();
        } catch (IOException | IllegalArgumentException e) {
            return Result.response(e);
        }
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
