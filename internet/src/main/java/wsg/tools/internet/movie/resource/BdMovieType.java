package wsg.tools.internet.movie.resource;

import wsg.tools.common.util.function.CodeSupplier;

/**
 * Types of the resources under {@link BdMovieSite}.
 *
 * @author Kingen
 * @since 2021/3/1
 */
public enum BdMovieType implements CodeSupplier {
    /**
     * @see <a href="https://www.bd2020.com/zx/index.htm">Latest</a>
     */
    LATEST("zx", 1328),
    /**
     * @see <a href="https://www.bd2020.com/gq/index.htm">HD</a>
     */
    HD("gq", 375),
    /**
     * @see <a href="https://www.bd2020.com/gy/index.htm">Mandarin</a>
     */
    MANDARIN("gy", 348),
    /**
     * @see <a href="https://www.bd2020.com/zy/index.htm">Micro Movies</a>
     */
    MICRO("zy", 1257),
    /**
     * @see <a href="https://www.bd2020.com/jd/index.htm">Classic</a>
     */
    CLASSIC("jd", 359),
    /**
     * @see <a href="https://www.bd2020.com/dh/index.htm">Animes</a>
     */
    ANIME("dh", 387);

    private final String code;
    private final int first;

    BdMovieType(String code, int first) {
        this.code = code;
        this.first = first;
    }

    public int first() {
        return first;
    }

    @Override
    public String getCode() {
        return code;
    }
}
