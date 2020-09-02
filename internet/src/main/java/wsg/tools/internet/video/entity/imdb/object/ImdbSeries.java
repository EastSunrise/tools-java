package wsg.tools.internet.video.entity.imdb.object;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;

/**
 * IMDb TV Series.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class ImdbSeries extends BaseImdbTitle {

    private Integer seasonsCount;
}