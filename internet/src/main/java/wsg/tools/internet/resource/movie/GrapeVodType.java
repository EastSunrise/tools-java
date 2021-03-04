package wsg.tools.internet.resource.movie;

import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Types of vod items in the {@link GrapeSite}.
 *
 * @author Kingen
 * @since 2021/3/4
 */
public enum GrapeVodType implements IntCodeSupplier {
    /**
     * @see <a href="https://www.putaoys.com/index.php?s=vod-type-id-1-p-1.html">Movies</a>
     */
    MOVIE(1),
    /**
     * @see <a href="https://www.putaoys.com/index.php?s=vod-type-id-2-p-1.html">Series</a>
     */
    SERIES(2),
    /**
     * @see <a href="https://www.putaoys.com/index.php?s=vod-type-id-3-p-1.html">Animes</a>
     */
    ANIME(3),
    /**
     * @see <a href="https://www.putaoys.com/index.php?s=vod-type-id-4-p-1.html">Varieties</a>
     */
    VARIETY(4);

    private final int code;

    GrapeVodType(int code) {this.code = code;}

    @Override
    public Integer getCode() {
        return code;
    }
}
