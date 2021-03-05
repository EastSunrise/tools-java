package wsg.tools.internet.movie.douban.api.pojo;

import java.util.List;
import lombok.Getter;
import wsg.tools.internet.movie.common.enums.MovieRole;

/**
 * Works of creators.
 *
 * @author Kingen
 * @since 2020/7/26
 */
@Getter
public class Work {

    private List<MovieRole> roles;
    private SimpleSubject subject;
}
