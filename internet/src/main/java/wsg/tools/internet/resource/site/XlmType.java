package wsg.tools.internet.resource.site;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.internet.common.FirstSupplier;
import wsg.tools.internet.resource.item.VideoType;

import javax.annotation.Nonnull;

/**
 * Types of the resources under {@link XlmSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public enum XlmType implements IntCodeSupplier, FirstSupplier<Integer> {
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz1.html">Mainland Movies</a>
     */
    MAINLAND(1, VideoType.MOVIE, 2),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz2.html">Hong Kong and Taiwan Movies</a>
     */
    HK_TW(2, VideoType.MOVIE, 86),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz3.html">Western Movies</a>
     */
    WEST(3, VideoType.MOVIE, 3),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz4.html">Japanese and Korean Movies</a>
     */
    JP_KR(4, VideoType.MOVIE, 10),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz5.html">Mainland Series</a>
     */
    MAINLAND_SERIES(5, VideoType.SERIES, 27),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz6.html">Hong Kong and Taiwan Series</a>
     */
    HK_TW_SERIES(6, VideoType.SERIES, 56),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz7.html">Japanese and Korean Series</a>
     */
    JP_KR_SERIES(7, VideoType.SERIES, 46),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz8.html">Western Series</a>
     */
    WEST_SERIES(8, VideoType.SERIES, 23),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz9.html">Varieties</a>
     */
    VARIETY(9, VideoType.VARIETY, 33),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz10.html">Animes</a>
     */
    ANIME(10, VideoType.ANIME, 7),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz11.html">3D Movies</a>
     */
    THREE_D(11, VideoType.THREE_D, 1),
    /**
     * @see <a href="https://www.xleimi.com/lanmu/xz12.html">TVB Series</a>
     */
    TVB(12, VideoType.SERIES, 68),
    ;

    private final int code;
    private final VideoType videoType;
    private final int first;

    XlmType(int code, VideoType videoType, int first) {
        this.code = code;
        this.videoType = videoType;
        this.first = first;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public VideoType getVideoType() {
        return videoType;
    }

    @Nonnull
    @Override
    public Integer first() {
        return first;
    }
}
