package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Object of a resource.
 *
 * @author Kingen
 * @since 2020/11/28
 */
@Getter
@Setter
public class ResourceDto extends BaseDto {

    private String url;
    private String title;
    private Map<String, List<LinkDto>> links;

}
