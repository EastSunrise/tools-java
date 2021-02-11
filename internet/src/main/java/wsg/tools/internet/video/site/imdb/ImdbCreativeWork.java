package wsg.tools.internet.video.site.imdb;

import java.time.Year;

/**
 * IMDb creative works.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ImdbCreativeWork extends ImdbTitle {

    private Year year;

    ImdbCreativeWork() {
    }

    public Year getYear() {
        return year;
    }

    void setYear(Year year) {
        this.year = year;
    }
}
