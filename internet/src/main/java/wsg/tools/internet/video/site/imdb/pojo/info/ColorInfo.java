package wsg.tools.internet.video.site.imdb.pojo.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.ColorEnum;
import wsg.tools.internet.video.site.imdb.pojo.base.BaseImdbInfo;

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
