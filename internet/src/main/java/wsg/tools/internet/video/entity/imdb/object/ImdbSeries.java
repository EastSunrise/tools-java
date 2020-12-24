package wsg.tools.internet.video.entity.imdb.object;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.entity.imdb.info.YearInfo;

import java.util.List;

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

    /**
     * Index of a given episode is array[currentSeason-1][currentEpisode].
     * <p>
     * Ep0 may be included if exists. It also means that length of each array is at least 1
     * even if all of the elements are null.
     */
    private List<String[]> episodes;
}