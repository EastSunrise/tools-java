package wsg.tools.internet.video.entity.gen.base;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Base response of PT Gen.
 *
 * @author Kingen
 * @since 2020/8/30
 */
@Getter
@Setter
public abstract class BaseGenResponse {

    private String sid;
    private String site;
    private Instant generateAt;

    private Boolean success;
    private Float cost;
    private String error;
    private String img;
    private String format;
    private String version;
    private String copyright;
}
