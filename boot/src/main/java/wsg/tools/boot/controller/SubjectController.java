package wsg.tools.boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.entity.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.SeasonEntity;
import wsg.tools.boot.pojo.entity.SeriesEntity;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.boot.service.intf.SubjectService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * API of video subjects.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Slf4j
@Controller
@RequestMapping("/video")
public class SubjectController extends AbstractController {

    private final SubjectService subjectService;
    private final ResourceService resourceService;

    @Autowired
    public SubjectController(SubjectService subjectService, ResourceService resourceService) {
        this.subjectService = subjectService;
        this.resourceService = resourceService;
    }

    /**
     * Import subjects from Douban of the given user.
     */
    @PostMapping(value = "/douban/import")
    @ResponseBody
    public BatchResult<Long> importDouban(long user, LocalDate since) {
        return subjectService.importDouban(user, since);
    }

    @GetMapping(path = "/movie/index")
    public String movies(Model model) {
        List<MovieEntity> entities = subjectService.listMovies().get();
        model.addAttribute("entities", entities);
        return "video/movie/index";
    }

    @GetMapping(path = "/movie/{id}/resources")
    public String movieResources(@PathVariable Long id, Model model) {
        GenericResult<MovieEntity> result = subjectService.getMovie(id);
        if (!result.isSuccess()) {
            return "error/notFound";
        }
        MovieEntity movieEntity = result.get();
        model.addAttribute("movie", movieEntity);
        model.addAttribute("items", resourceService.search(movieEntity.getTitle(), movieEntity.getDbId(), movieEntity.getImdbId()));
        return "video/movie/resources";
    }

    @RequestMapping("/series/index")
    public String series(Model model) {
        model.addAttribute("entities", subjectService.listSeries().get());
        return "video/series/index";
    }

    @GetMapping(path = "/series/{id}/resources")
    public String seriesResources(@PathVariable Long id, Model model) {
        GenericResult<SeriesEntity> result = subjectService.getSeries(id);
        if (!result.isSuccess()) {
            return "error/notFound";
        }
        SeriesEntity seriesEntity = result.get();
        model.addAttribute("series", seriesEntity);
        List<SeasonEntity> seasons = subjectService.getSeasonsBySeries(id);
        model.addAttribute("seasons", seasons);
        Set<ResourceItemEntity> items = resourceService.search(seriesEntity.getTitle(), null, seriesEntity.getImdbId());
        seasons.forEach(seasonEntity -> items.addAll(resourceService.search(null, seasonEntity.getDbId(), null)));
        model.addAttribute("items", items);
        return "video/series/resources";
    }
}
