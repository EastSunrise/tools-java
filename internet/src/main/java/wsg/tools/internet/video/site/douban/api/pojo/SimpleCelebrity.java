package wsg.tools.internet.video.site.douban.api.pojo;

import lombok.Getter;

/**
 * A simple celebrity.
 *
 * @author Kingen
 * @since 2020/7/27
 */
@Getter
public class SimpleCelebrity {

    private Long id;
    private String name;
    private String nameEn;
    private String alt;
    private Image avatars;
}
