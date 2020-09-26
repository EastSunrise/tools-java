package wsg.tools.internet.video.entity.imdb.object;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.entity.imdb.info.YearInfo;

/**
 * IMDb TV Series.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class ImdbSeries extends BaseImdbTitle {

    private YearInfo yearInfo;
    private ImdbVideoObject trailer;
}