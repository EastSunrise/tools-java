package wsg.tools.internet.video.site.imdb.pojo.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.site.imdb.pojo.base.BaseImdbInfo;

/**
 * Info of aspect ratio, like 4:3, 16:9
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class AspectRatio extends BaseImdbInfo {

    private double width;
    private double height;
}
