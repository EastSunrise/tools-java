package wsg.tools.internet.video.site.imdb;

import javax.annotation.Nullable;
import java.time.Year;

/**
 * IMDb Movie.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ImdbMovie extends ImdbTitle {

    private Year year;

    @Nullable
    public Year getYear() {
        return year;
    }

    void setYear(Year year) {
        this.year = year;
    }
}
