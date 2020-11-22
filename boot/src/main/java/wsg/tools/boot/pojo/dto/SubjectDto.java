package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseDto;

/**
 * Subject object.
 *
 * @author Kingen
 * @since 2020/11/22
 */
@Getter
@Setter
public class SubjectDto extends BaseDto {

    private Long id;
    private String title;
    private boolean archived;
}
