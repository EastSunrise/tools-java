package wsg.tools.internet.resource.entity.title;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.common.VideoTypeEnum;

/**
 * Detail with year and type.
 *
 * @author Kingen
 * @since 2020/10/20
 */
@Setter
@Getter
public class SimpleDetail extends BaseDetail {
    private Integer year;
    private VideoTypeEnum type;
}
