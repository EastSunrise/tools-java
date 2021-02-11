package wsg.tools.internet.video.site.imdb.pojo.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.site.imdb.pojo.base.BaseImdbInfo;

/**
 * Info of a camera.
 * <p>
 * Format: Camera, Lens.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class Camera extends BaseImdbInfo {

    private String camera;
    private String lens;
}
