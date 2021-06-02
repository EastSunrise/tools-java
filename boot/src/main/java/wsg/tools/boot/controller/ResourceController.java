package wsg.tools.boot.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.dto.ResourceQueryDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.service.impl.ResourceDto;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.boot.service.intf.SubjectService;

/**
 * API of resources.
 *
 * @author Kingen
 * @since 2020/11/19
 */
@Controller
@RequestMapping("/video/resource")
public class ResourceController extends AbstractController {

    private final ResourceService resourceService;
    private final SubjectService subjectService;

    @Autowired
    public ResourceController(ResourceService resourceService, SubjectService subjectService) {
        this.resourceService = resourceService;
        this.subjectService = subjectService;
    }

    @PostMapping(path = "/movie")
    public String movieResources(Long id, String key, Model model) {
        Optional<MovieEntity> optional = subjectService.getMovie(id);
        if (optional.isEmpty()) {
            return ERROR_NOT_FOUND;
        }
        MovieEntity movieEntity = optional.get();
        model.addAttribute("movie", movieEntity);
        if (StringUtils.isBlank(key)) {
            key = movieEntity.getZhTitle();
        }
        model.addAttribute("items",
            resourceService.search(key, movieEntity.getDbId(), movieEntity.getImdbId()));
        return "video/movie/resources";
    }

    @PostMapping(path = "/series")
    public String seriesResources(Long id, String key, Model model) {
        Pair<SeriesEntity, List<SeasonEntity>> pair = subjectService.getSeries(id);
        if (pair.getLeft() == null) {
            return ERROR_NOT_FOUND;
        }
        SeriesEntity seriesEntity = pair.getLeft();
        model.addAttribute("series", seriesEntity);
        List<SeasonEntity> seasons = pair.getRight();
        model.addAttribute("seasons", seasons);
        if (StringUtils.isBlank(key)) {
            key = seriesEntity.getZhTitle();
        }
        String imdbId = seriesEntity.getImdbId();
        List<ResourceItemEntity> entities = resourceService.search(key, null, imdbId);
        Set<ResourceItemEntity> items = new HashSet<>(entities);
        seasons.forEach(seasonEntity ->
            items.addAll(resourceService.search(null, seasonEntity.getDbId(), null))
        );
        model.addAttribute("items", items);
        return "video/series/resources";
    }

    @GetMapping("/index")
    public String links(Model model, ResourceQueryDto query) {
        List<ResourceItemEntity> items = resourceService
            .search(query.getKey(), query.getDbId(), query.getImdbId(), query.getChosen());
        List<ResourceDto> resources = resourceService.getResources(items);
        model.addAttribute("resources", resources);
        model.addAttribute("query", query);
        return "video/resource/index";
    }

    @PostMapping("/check")
    @ResponseBody
    public long identifyResources(@RequestBody List<ResourceCheckDto> checkDto) {
        return resourceService.identifyResources(checkDto);
    }
}