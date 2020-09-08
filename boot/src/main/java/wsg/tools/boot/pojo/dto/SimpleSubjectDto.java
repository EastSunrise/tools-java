package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseDto;

/**
 * Object of id info.
 *
 * @author Kingen
 * @since 2020/8/7
 */
@Getter
@Setter
public class SimpleSubjectDto extends BaseDto {
    private Long dbId;
    private String imdbId;
}
