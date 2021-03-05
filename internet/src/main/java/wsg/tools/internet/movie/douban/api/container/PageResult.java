package wsg.tools.internet.movie.douban.api.container;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import lombok.Getter;

/**
 * Result with paged info.
 *
 * @param <C> argument type of content
 * @author Kingen
 * @since 2020/7/25
 */
@Getter
class PageResult<C> {

    private int start;
    private int count;
    private int total;
    @JsonAlias({"subjects", "reviews", "comments", "works", "photos"})
    private List<C> content;
}
