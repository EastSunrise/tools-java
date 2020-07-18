package wsg.tools.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import wsg.tools.boot.common.WatchlistReader;
import wsg.tools.boot.pojo.base.PageResult;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.dto.QuerySubjectDto;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.internet.video.enums.MarkEnum;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

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
        result.put("archivedEnums", ArchivedEnum.values());
        result.put("marks", MarkEnum.values());
        return result.toResponse(assembler);
    }

    @RequestMapping(value = "/douban", method = RequestMethod.POST)
    public ResponseEntity<?> updateDouban(long user, LocalDate since) {
        return subjectService.importDouban(user, since).toResponse();
    }

    @RequestMapping(value = "/imdb", method = RequestMethod.POST)
    public ResponseEntity<?> importImdb(MultipartFile file) {
        try {
            List<String> ids = new WatchlistReader().readMultipartFile(file);
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

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
