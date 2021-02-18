package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Object of a resource.
 *
 * @author Kingen
 * @since 2020/11/28
 */
@Getter
@Setter
public class ResourceDto extends BaseDto {

    private String title;
    private String url;
    private List<LinkDto> links;
}
