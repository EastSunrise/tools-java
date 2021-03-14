package wsg.tools.internet.resource.movie;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;

/**
 * The subtype that an item belongs to in the {@link Y80sSite}.
 *
 * @author Kingen
 * @since 2021/3/12
 */
public enum Y80sType implements IntCodeSupplier, TextSupplier {
    /**
     * @see <a href="http://m.y80s.com/movie/1-0-0-0-0-0-0">Movies</a>
     */
    MOVIE(1, "movie"),
    /**
     * @see <a href="http://m.y80s.com/ju/9-0-0-0-0-0-0">Mainland Series</a>
     * @see <a href="http://m.y80s.com/ju/10-0-0-0-0-0-0">Hong Kong and Taiwan Series</a>
     * @see <a href="http://m.y80s.com/ju/11-0-0-0-0-0-0">Japanese and Korean Series</a>
     * @see <a href="http://m.y80s.com/ju/12-0-0-0-0-0-0">Western Series</a>
     * @see <a href="http://m.y80s.com/ju/13-0-0-0-0-0-0">Other Series</a>
     */
    SERIES(2, "ju"),
    /**
     * @see <a href="http://m.y80s.com/zy/4-0-0-0-0-0-0">Variety</a>
     */
    VARIETY(4, "zy"),
    /**
     * @see <a href="http://m.y80s.com/mv/5-0-0-0-0-0-0">MV</a>
     */
    MV(5, "mv"),
    /**
     * @see <a href="http://m.y80s.com/video/6-0-0-0-0-0-0">Videos</a>
     */
    VIDEO(6, "video"),
    /**
     * @see <a href="http://m.y80s.com/course/7-0-0-0-0-0-0">Courses</a>
     */
    COURSE(7, "course"),
    /**
     * @see <a href="http://m.y80s.com/trailer/8-0-0-0-0-0-0">Trailers</a>
     */
    TRAILER(8, "trailer"),
    /**
     * @see <a href="http://m.y80s.com/dm/14-0-0-0-0-0-0">Animes</a>
     */
    ANIME(14, "dm"),
    /**
     * @see <a href="http://m.y80s.com/weidianying/15-0-0-0-0-0-0">Micro Movies</a>
     */
    MICRO(15, "weidianying"),
    ;

    private final int id;
    private final String text;

    Y80sType(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Integer getCode() {
        return id;
    }
}
