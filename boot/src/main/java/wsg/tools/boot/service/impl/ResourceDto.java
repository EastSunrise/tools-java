package wsg.tools.boot.service.impl;

import lombok.Getter;
import wsg.tools.boot.pojo.dto.BaseDto;
import wsg.tools.boot.pojo.dto.LinkDto;

import java.util.List;

/**
 * Object of a resource.
 *
 * @author Kingen
 * @since 2020/11/28
 */
@Getter
public class ResourceDto extends BaseDto {

    private final String title;
    private final String url;
    private List<LinkDto> links;

    ResourceDto(String title, String url) {
        this.title = title;
        this.url = url;
    }

    void setLinks(List<LinkDto> links) {
        this.links = links;
    }
}