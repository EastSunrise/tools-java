package wsg.tools.internet.movie.imdb;

/**
 * IMDb creative works.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ImdbCreativeWork extends ImdbTitle {

    private Integer year;

    ImdbCreativeWork() {
    }

    public Integer getYear() {
        return year;
    }

    void setYear(int year) {
        this.year = year;
    }
}
