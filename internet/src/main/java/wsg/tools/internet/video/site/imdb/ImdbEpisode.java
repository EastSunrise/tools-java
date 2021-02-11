package wsg.tools.internet.video.site.imdb;

import javax.annotation.Nullable;
import java.time.Year;

/**
 * IMDb TV Episode.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ImdbEpisode extends ImdbTitle {

    private Year year;
    private String seriesId;

    @Nullable
    public Year getYear() {
        return year;
    }

    void setYear(Year year) {
        this.year = year;
    }

    public String getSeriesId() {
        return seriesId;
    }

    void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }
}