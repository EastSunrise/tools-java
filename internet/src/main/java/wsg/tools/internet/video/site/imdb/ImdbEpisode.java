package wsg.tools.internet.video.site.imdb;

import javax.annotation.Nullable;

/**
 * IMDb TV Episode.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ImdbEpisode extends ImdbTitle {

    private Integer year;
    private String seriesId;

    @Nullable
    public Integer getYear() {
        return year;
    }

    void setYear(int year) {
        this.year = year;
    }

    public String getSeriesId() {
        return seriesId;
    }

    void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }
}