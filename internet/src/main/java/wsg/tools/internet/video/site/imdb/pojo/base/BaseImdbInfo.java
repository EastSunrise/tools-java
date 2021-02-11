package wsg.tools.internet.video.site.imdb.pojo.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Base class for info of a title.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public abstract class BaseImdbInfo {

    private List<String> attributes;
}
