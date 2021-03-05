package wsg.tools.internet.movie.imdb;

import java.util.List;
import wsg.tools.internet.movie.common.RangeYear;
import wsg.tools.internet.movie.imdb.pojo.object.ImdbVideoObject;

/**
 * IMDb TV Series.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ImdbSeries extends ImdbTitle {

    private ImdbVideoObject trailer;
    private RangeYear rangeYear;

    /**
     * Index of a given episode is array[currentSeason-1][currentEpisode].
     * <p>
     * Ep0 may be included if exists. It also means that length of each array is at least 1 even if
     * all of the elements are null.
     */
    private List<String[]> episodes;

    public RangeYear getRangeYear() {
        return rangeYear;
    }

    void setRangeYear(RangeYear rangeYear) {
        this.rangeYear = rangeYear;
    }

    public List<String[]> getEpisodes() {
        return episodes;
    }

    void setEpisodes(List<String[]> episodes) {
        this.episodes = episodes;
    }
}