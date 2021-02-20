package wsg.tools.internet.video.site.douban.api.pojo;

import lombok.Getter;

/**
 * Avatars of users .
 *
 * @author Kingen
 * @since 2020/9/19
 */
@Getter
public class Avatar extends Image {

    private String median;
    private String icon;
    private String raw;
}