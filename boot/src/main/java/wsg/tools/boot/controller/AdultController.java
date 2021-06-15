package wsg.tools.boot.controller;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import wsg.tools.boot.dao.jpa.mapper.JaAdultVideoRepository;
import wsg.tools.boot.pojo.entity.adult.JaAdultVideoEntity;

/**
 * API of adult entries.
 *
 * @author Kingen
 * @since 2021/6/8
 */
@Controller
@RequestMapping("/adult")
public class AdultController extends AbstractController {

    private final JaAdultVideoRepository repository;

    @Autowired
    public AdultController(JaAdultVideoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/entry")
    public String getEntry(Long id, String sn, Model model) {
        Optional<JaAdultVideoEntity> optional;
        if (id == null) {
            if (StringUtils.isBlank(sn)) {
                Page<JaAdultVideoEntity> page = repository.findAll(PageRequest.of(0, 2));
                if (!page.hasContent()) {
                    return ERROR_NOT_FOUND;
                }
                List<JaAdultVideoEntity> content = page.getContent();
                model.addAttribute("current", content.get(0));
                if (content.size() > 1) {
                    model.addAttribute("next", content.get(1));
                }
                return "adult/entry";
            }
            optional = repository.findBySerialNum(sn);
        } else {
            if (StringUtils.isBlank(sn)) {
                optional = repository.findById(id);
            } else {
                optional = repository.findBySerialNumAndId(sn, id);
            }
        }
        if (optional.isEmpty()) {
            return ERROR_NOT_FOUND;
        }
        JaAdultVideoEntity entity = optional.get();
        model.addAttribute("current", entity);
        long cid = entity.getId();
        Optional<JaAdultVideoEntity> next = repository.getFirstByIdGreaterThan(cid);
        next.ifPresent(e -> model.addAttribute("next", e));
        Optional<JaAdultVideoEntity> previous = repository.getFirstByIdLessThanOrderByIdDesc(cid);
        previous.ifPresent(e -> model.addAttribute("previous", e));
        return "adult/entry";
    }
}
