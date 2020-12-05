package wsg.tools.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import wsg.tools.boot.common.util.BeanUtilExt;
import wsg.tools.boot.pojo.dto.LinkDto;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.dto.ResourceQueryDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.internet.resource.download.Thunder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping(value = "/index")
    public String links(Model model, ResourceQueryDto query) {
        Stream<ResourceItemEntity> items = resourceService.search(query.getKey(), query.getDbId(), query.getImdbId()).stream();
        if (query.getChosen() != null) {
            final boolean chosen = query.getChosen();
            items = items.filter(item -> item.getIdentified() == chosen);
        }
        List<ResourceLinkEntity> linkEntities = resourceService.getLinks(items.map(ResourceItemEntity::getUrl).collect(Collectors.toSet()));
        Map<String, List<LinkDto>> links = linkEntities.stream().map(link -> {
            LinkDto linkDto = BeanUtilExt.convert(link, LinkDto.class);
            if (linkDto.getFilename() == null) {
                linkDto.setFilename(linkDto.getTitle());
            }
            linkDto.setThunder(Thunder.encodeThunder(linkDto.getUrl()));
            return linkDto;
        }).distinct().collect(Collectors.groupingBy(linkDto -> linkDto.getType().getText()));
        model.addAttribute("links", links);
        model.addAttribute("query", query);
        return "video/resource/index";
    }

    @PostMapping(value = "/check")
    @ResponseBody
    public long checkResources(@RequestBody List<ResourceCheckDto> checkDto) {
        return resourceService.check(checkDto);
    }
}