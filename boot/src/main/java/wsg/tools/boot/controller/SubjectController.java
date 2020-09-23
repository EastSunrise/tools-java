package wsg.tools.boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.excel.ExcelTemplate;

import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
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

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response) throws IntrospectionException, IOException {
        List<SubjectEntity> entities = subjectService.export().getData();
        ExcelTemplate<SubjectEntity> template = ExcelTemplate.create(SubjectEntity.class);
        exportXlsx(response, entities, "Subjects", template.getWriters());
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
            BatchResult<String> batchResult = subjectService.importManually(keyValues);
            batchResult.put("Wrong Rows", all.size() - keyValues.size());
            return batchResult.toResponse();
        } catch (IOException | IllegalArgumentException e) {
            return Result.response(e);
        }
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
