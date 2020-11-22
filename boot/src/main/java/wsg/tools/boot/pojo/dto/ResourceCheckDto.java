package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseDto;

/**
 * Object for checking a resource.
 *
 * @author Kingen
 * @since 2020/11/21
 */
@Getter
@Setter
public class ResourceCheckDto extends BaseDto {

    private String url;
    private Long dbId;
    private String imdbId;
}
