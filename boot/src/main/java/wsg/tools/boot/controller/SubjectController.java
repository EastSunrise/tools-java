package wsg.tools.boot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import wsg.tools.boot.entity.base.Result;
import wsg.tools.boot.entity.subject.dto.SubjectDto;
import wsg.tools.boot.entity.subject.enums.ArchivedEnum;
import wsg.tools.boot.entity.subject.enums.StatusEnum;
import wsg.tools.boot.entity.subject.enums.SubtypeEnum;
import wsg.tools.boot.entity.subject.query.QuerySubject;
import wsg.tools.boot.service.intf.SubjectService;

import java.util.List;

/**
 * Management of video subjects.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Controller
@RequestMapping("/video")
public class SubjectController extends AbstractController {

    private SubjectService subjectService;

    @RequestMapping("/index")
    public String index(Model model, QuerySubject querySubject) {
        Page<SubjectDto> page = subjectService.list(querySubject);
        model.addAttribute("page", page);
        List<SubjectDto> records = page.getRecords();
        model.addAttribute("records", records);
        model.addAttribute("querySubject", querySubject);
        model.addAttribute("statuses", StatusEnum.values());
        model.addAttribute("archivedArray", ArchivedEnum.values());
        model.addAttribute("subtypes", SubtypeEnum.values());
        return "video/index";
    }

    @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
    @ResponseBody
    public Result updateInfo(long id) {
        return subjectService.updateInfo(id);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateById(SubjectDto subject) {
        if (subjectService.updateById(subject)) {
            return Result.success();
        }
        return Result.fail("Failed to update info: %d", subject.getId());
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    @ResponseBody
    public Result play(long id) {
        return subjectService.play(id);
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
