package wsg.tools.internet.video.site.imdb.pojo.base;

import lombok.Getter;

import java.util.List;

/**
 * Base class for info of a title.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
public abstract class BaseImdbInfo {

    private List<String> attributes;
}
