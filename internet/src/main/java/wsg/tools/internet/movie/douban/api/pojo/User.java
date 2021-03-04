package wsg.tools.internet.movie.douban.api.pojo;

import lombok.Getter;

/**
 * Users from douban.
 *
 * @author Kingen
 * @since 2020/7/18
 */
@Getter
public class User {
    private Long uid;
    private Avatar avatar;
    private String signature;
    private String alt;
    private Long id;
    private String name;
}
