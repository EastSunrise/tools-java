package wsg.tools.internet.movie.douban.api.pojo;

import lombok.Getter;
import wsg.tools.internet.movie.common.enums.RoleEnum;

import java.util.List;

/**
 * Works of creators.
 *
 * @author Kingen
 * @since 2020/7/26
 */
@Getter
public class Work {
    private List<RoleEnum> roles;
    private SimpleSubject subject;
}
