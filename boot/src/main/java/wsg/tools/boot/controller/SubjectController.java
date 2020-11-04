package wsg.tools.boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.io.excel.ExcelTemplate;

import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping(value = "/douban/import", method = RequestMethod.POST)
    public void updateDouban(HttpServletResponse response, long user, LocalDate since) {
        BatchResult<Long> result = subjectService.importDouban(user, since);
        exportXlsx(response, result.getFails(), "import_douban", "id", "reason");
    }

    @RequestMapping(value = "/relation", method = RequestMethod.POST)
    public Result importRelations(MultipartFile file) {
        try {
            List<DefaultKeyValue<String, Long>> all = readXlsx(file, NOT_FOUND.getReaders(), DefaultKeyValue::new);
            List<DefaultKeyValue<String, Long>> keyValues = all.stream().filter(keyValue -> keyValue.getKey() != null).collect(Collectors.toList());
            return subjectService.importManually(keyValues);
        } catch (IOException | IllegalArgumentException e) {
            return Result.fail(e);
        }
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
