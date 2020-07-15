package wsg.tools.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import wsg.tools.boot.common.WatchlistReader;
import wsg.tools.boot.pojo.base.BatchResult;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.dto.QuerySubjectDto;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.result.SubjectsResult;
import wsg.tools.boot.service.intf.SubjectService;

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
    public SubjectsResult index(QuerySubjectDto querySubject, Pageable pageable) {
        return subjectService.list(querySubject, pageable);
    }

    @RequestMapping(value = "/douban", method = RequestMethod.POST)
    public BatchResult updateDouban(long user, LocalDate since) {
        return subjectService.importDouban(user, since);
    }

    @RequestMapping(value = "/imdb", method = RequestMethod.POST)
    public BatchResult importImdb(MultipartFile file) {
        try {
            List<String> ids = new WatchlistReader().readMultipartFile(file);
            return subjectService.importImdbIds(ids);
        } catch (IOException | IllegalArgumentException e) {
            return new BatchResult(e);
        }
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    public Result play(long id) {
        return subjectService.play(id);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateById(SubjectDto subject) {
        subjectService.updateById(subject);
        return Result.success();
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
