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
    private final boolean identified;
    private List<LinkDto> links;

    ResourceDto(String title, String url, boolean identified) {
        this.title = title;
        this.url = url;
        this.identified = identified;
    }

    void setLinks(List<LinkDto> links) {
        this.links = links;
    }
}
