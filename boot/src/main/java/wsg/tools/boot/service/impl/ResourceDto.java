package wsg.tools.boot.service.impl;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.dto.BaseDto;
import wsg.tools.boot.pojo.dto.LinkDto;

/**
 * Object of a resource.
 *
 * @author Kingen
 * @since 2020/11/28
 */
@Getter
public class ResourceDto extends BaseDto {

    private static final long serialVersionUID = 2117167698028536207L;

    private final String title;
    private final String url;
    private final boolean identified;

    @Setter(AccessLevel.PACKAGE)
    private List<LinkDto> links;

    ResourceDto(String title, String url, boolean identified) {
        this.title = title;
        this.url = url;
        this.identified = identified;
    }
}
