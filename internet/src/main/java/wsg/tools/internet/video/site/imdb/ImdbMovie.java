package wsg.tools.internet.video.site.imdb;

import javax.annotation.Nullable;

/**
 * IMDb Movie.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ImdbMovie extends ImdbTitle {

    private Integer year;

    @Nullable
    public Integer getYear() {
        return year;
    }

    void setYear(int year) {
        this.year = year;
    }
}
