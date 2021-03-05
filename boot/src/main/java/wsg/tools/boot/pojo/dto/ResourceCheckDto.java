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

    private static final long serialVersionUID = -3550972446159103724L;

    private Long id;
    private Long dbId;
    private String imdbId;
}
