package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Object for checking a resource.
 *
 * @author Kingen
 * @since 2020/11/21
 */
@Getter
@Setter
public class ResourceCheckDto extends BaseDto {

    private Long id;
    private Long dbId;
    private String imdbId;
}
