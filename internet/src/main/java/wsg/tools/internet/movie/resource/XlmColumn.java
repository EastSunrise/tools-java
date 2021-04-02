package wsg.tools.internet.movie.resource;

import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * The column that an item belongs to in the {@link XlmSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public enum XlmColumn implements IntCodeSupplier {
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz1.html">Mainland Movies</a>
     */
    MAINLAND_MOVIE(1, 2),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz2.html">Hong Kong and Taiwan Movies</a>
     */
    HK_TW_MOVIE(2, 86),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz3.html">Western Movies</a>
     */
    WEST_MOVIE(3, 3),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz4.html">Japanese and Korean Movies</a>
     */
    JP_KR_MOVIE(4, 10),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz5.html">Mainland Series</a>
     */
    MAINLAND_SERIES(5, 27),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz6.html">Hong Kong and Taiwan Series</a>
     */
    HK_TW_SERIES(6, 56),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz7.html">Japanese and Korean Series</a>
     */
    JP_KR_SERIES(7, 46),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz8.html">Western Series</a>
     */
    WEST_SERIES(8, 23),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz9.html">Varieties</a>
     */
    VARIETY(9, 33),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz10.html">Animes</a>
     */
    ANIME(10, 7),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz11.html">3D Movies</a>
     */
    THREE_D(11, 1),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz12.html">TVB Series</a>
     */
    TVB(12, 68);

    private final int code;
    private final int first;

    XlmColumn(int code, int first) {
        this.code = code;
        this.first = first;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public int first() {
        return first;
    }
}
