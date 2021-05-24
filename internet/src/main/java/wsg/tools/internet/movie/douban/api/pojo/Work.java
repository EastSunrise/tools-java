package wsg.tools.internet.movie.douban.api.pojo;

import java.util.List;
import lombok.Getter;

/**
 * Works of creators.
 *
 * @author Kingen
 * @since 2020/7/26
 */
@Getter
public class Work {

    private List<String> roles;
    private SimpleSubject subject;
}
