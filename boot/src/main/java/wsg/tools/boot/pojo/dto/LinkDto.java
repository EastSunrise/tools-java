package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.common.enums.ResourceType;

/**
 * Object of a link.
 *
 * @author Kingen
 * @since 2020/11/28
 */
@Getter
@Setter
public class LinkDto extends BaseDto {

    private static final long serialVersionUID = -8770306015230171571L;

    private ResourceType type;
    private String url;
    private String title;
    private String filename;
    private Long length;
    private String thunder;
}
