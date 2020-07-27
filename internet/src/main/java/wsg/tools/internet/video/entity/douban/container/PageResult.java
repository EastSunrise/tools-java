package wsg.tools.internet.video.entity.douban.container;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Result with paged info.
 *
 * @param <C> argument type of content
 * @author Kingen
 * @since 2020/7/25
 */
@Setter
@Getter
public class PageResult<C> {

    private int start;
    private int count;
    private int total;
    @JsonAlias({"subjects", "reviews", "comments", "works", "photos"})
    private List<C> content;
}
