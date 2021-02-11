package wsg.tools.internet.video.site.imdb;

import wsg.tools.internet.video.site.imdb.pojo.info.YearInfo;
import wsg.tools.internet.video.site.imdb.pojo.object.ImdbVideoObject;

import java.util.List;

/**
 * IMDb TV Series.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ImdbSeries extends ImdbTitle {

    private YearInfo yearInfo;
    private ImdbVideoObject trailer;

    /**
     * Index of a given episode is array[currentSeason-1][currentEpisode].
     * <p>
     * Ep0 may be included if exists. It also means that length of each array is at least 1
     * even if all of the elements are null.
     */
    private List<String[]> episodes;

    public YearInfo getYearInfo() {
        return yearInfo;
    }

    void setYearInfo(YearInfo yearInfo) {
        this.yearInfo = yearInfo;
    }

    public List<String[]> getEpisodes() {
        return episodes;
    }

    void setEpisodes(List<String[]> episodes) {
        this.episodes = episodes;
    }
}