package wsg.tools.internet.movie.resource;

import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * The subtype that an item belongs to in the {@link XlcSite}.
 *
 * @author Kingen
 * @since 2021/3/12
 */
public enum XlcType implements IntCodeSupplier {

    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-3.html">Animes</a>
     */
    ANIME(3),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-4.html">Variety</a>
     */
    VARIETY(4),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-5.html">4K</a>
     */
    FOUR_K(5),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-6.html">1080P</a>
     */
    HD(6),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-7.html">Other Movies</a>
     */
    OTHER_MOVIE(7),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-8.html">Action Movies</a>
     */
    ACTION_MOVIE(8),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-9.html">Comedy Movies</a>
     */
    COMEDY_MOVIE(9),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-10.html">Romance Movies</a>
     */
    ROMANCE_MOVIE(10),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-11.html">Sci-Fi Movies</a>
     */
    SCI_FI_MOVIE(11),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-12.html">Horror Movies</a>
     */
    HORROR_MOVIE(12),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-13.html">Drama Movies</a>
     */
    DRAMA_MOVIE(13),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-14.html">War Movies</a>
     */
    WAR_MOVIE(14),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-15.html">Mainland Series</a>
     */
    MAINLAND_SERIES(15),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-16.html">Hong Kong and Taiwan Series</a>
     */
    HK_TW_SERIES(16),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-17.html">Western Series</a>
     */
    WEST_SERIES(17),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-18.html">Japanese and Korean Series</a>
     */
    JP_KR_SERIES(18),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-19.html">Southeast Asian Series</a>
     */
    SEA_SERIES(19),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-20.html">3D</a>
     */
    THREE_D(20),
    /**
     * @see <a href="https://www.xunleicang.in/vod-show-id-21.html">Mandarin</a>
     */
    MANDARIN(21);

    private final int id;

    XlcType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public Integer getCode() {
        return id;
    }
}
