package wsg.tools.internet.video.site.adult;

import lombok.Getter;

/**
 * An adult video.
 *
 * @author Kingen
 * @since 2021/2/23
 */
@Getter
public class BasicAdultVideo {

    private final String code;
    private final String image;

    BasicAdultVideo(String code, String image) {
        this.code = code;
        this.image = image;
    }
}
