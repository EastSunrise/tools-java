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
    private final String cover;

    BasicAdultVideo(String code, String cover) {
        this.code = code;
        this.cover = cover;
    }
}
