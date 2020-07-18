package wsg.tools.internet.video.entity.douban;

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
public class DoubanUser {
    private String uid;
    private String avatar;
    private String signature;
    private String alt;
    private Long id;
    private String name;
}
