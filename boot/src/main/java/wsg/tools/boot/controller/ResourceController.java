package wsg.tools.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.service.intf.ResourceService;

import java.util.List;

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

    @PostMapping(value = "/check")
    @ResponseBody
    public long checkResources(@RequestBody List<ResourceCheckDto> checkDto) {
        return resourceService.check(checkDto);
    }
}