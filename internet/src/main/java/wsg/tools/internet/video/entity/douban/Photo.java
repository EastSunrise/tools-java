package wsg.tools.internet.video.entity.douban;

import lombok.Getter;
import lombok.Setter;

/**
 * Photos of subjects or celebrities.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Setter
@Getter
public class Photo {
    private String thumb;
    private String image;
    private String cover;
    private String alt;
    private Long id;
    private String icon;
}
