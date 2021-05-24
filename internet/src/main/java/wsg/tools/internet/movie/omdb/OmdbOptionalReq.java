package wsg.tools.internet.movie.omdb;

import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.movie.common.VideoConstants;
import wsg.tools.internet.movie.imdb.MovieType;

/**
 * The request with optional parameters when accessing to the OMDb API.
 *
 * @author Kingen
 * @since 2021/5/21
 */
public class OmdbOptionalReq {

    private MovieType type;
    private int year;

    public MovieType getType() {
        return type;
    }

    public void setType(MovieType type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = AssertUtils.requireRange(year, VideoConstants.MOVIE_START_YEAR, null);
    }
}
