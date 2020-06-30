package wsg.tools.internet.video.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Object of a celebrity.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Setter
@Getter
public class Celebrity {
    private Image avatars;
    private String nameEn;
    private String name;
    private String alt;
    private Long id;
}
