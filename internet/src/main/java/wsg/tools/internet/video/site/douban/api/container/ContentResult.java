package wsg.tools.internet.video.site.douban.api.container;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

/**
 * Result of content of a subject or a celebrity.
 *
 * @param <O> type of the owner
 * @param <C> type of content
 * @author Kingen
 * @since 2020/7/26
 */
@Setter
@Getter
public class ContentResult<O, C> extends PageResult<C> {
    @JsonAlias({"subject", "celebrity"})
    private O owner;
    private int nextStart;
}
