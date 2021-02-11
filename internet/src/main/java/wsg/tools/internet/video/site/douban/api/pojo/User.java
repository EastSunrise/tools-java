package wsg.tools.internet.video.site.douban.api.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Users from douban.
 *
 * @author Kingen
 * @since 2020/7/18
 */
@Setter
@Getter
public class User {
    private Long uid;
    private Avatar avatar;
    private String signature;
    private String alt;
    private Long id;
    private String name;
}
