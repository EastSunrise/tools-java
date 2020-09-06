package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbInfo;
import wsg.tools.internet.video.enums.ColorEnum;

/**
 * Color Info.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class ColorInfo extends BaseImdbInfo {

    private ColorEnum color;
}
