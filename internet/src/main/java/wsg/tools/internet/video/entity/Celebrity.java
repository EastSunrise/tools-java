package wsg.tools.internet.video.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

/**
 * Celebrities of subjects.
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
    @JsonAlias("id")
    private Long dbId;
}
