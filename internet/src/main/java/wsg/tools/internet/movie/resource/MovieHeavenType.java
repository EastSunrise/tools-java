package wsg.tools.internet.movie.resource;

import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * The subtype that an item belongs to in {@link MovieHeavenSite}.
 *
 * @author Kingen
 * @since 2021/3/12
 */
public enum MovieHeavenType implements IntCodeSupplier {
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-3-pg-1.html">Variety</a>
     */
    VARIETY(3),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-4-pg-1.html">Animes</a>
     */
    ANIME(4),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-5-pg-1.html">720P</a>
     */
    BD(5),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-6-pg-1.html">1080P</a>
     */
    HD(6),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-7-pg-1.html">3D</a>
     */
    THREE_D(7),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-8-pg-1.html">Mandarin</a>
     */
    MANDARIN(8),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-9-pg-1.html">Action Movies</a>
     */
    ACTION_MOVIE(9),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-10-pg-1.html">Comedy Movies</a>
     */
    COMEDY_MOVIE(10),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-11-pg-1.html">Romance Movies</a>
     */
    ROMANCE_MOVIE(11),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-12-pg-1.html">Sci-Fi Movies</a>
     */
    SCI_FI_MOVIE(12),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-13-pg-1.html">Horror Movies</a>
     */
    HORROR_MOVIE(13),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-14-pg-1.html">Drama Movies</a>
     */
    Drama_MOVIE(14),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-15-pg-1.html">War Movies</a>
     */
    WAR_MOVIE(15),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-16-pg-1.html">Unknown-Type</a>
     */
    UNKNOWN_TYPE(16),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-17-pg-1.html">Mainland Series</a>
     */
    MAINLAND_SERIES(17),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-18-pg-1.html">Hong Kong and Taiwan
     * Series</a>
     */
    HK_TW_SERIES(18),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-19-pg-1.html">Western Series</a>
     */
    WEST_SERIES(19),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-20-pg-1.html">Japanese and Korean Series</a>
     */
    JP_KR_SERIES(20),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-21-pg-1.html">Southeast Asian Series</a>
     */
    SEA_SERIES(21),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-22-pg-1.html">4K</a>
     */
    FOUR_K(22),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-23-pg-1.html">Crime Movies</a>
     */
    CRIME_MOVIE(23),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-25-pg-1.html">Mystery Movies</a>
     */
    MYSTERY_MOVIE(25),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-26-pg-1.html">Disaster Movies</a>
     */
    DISASTER_MOVIE(26),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-27-pg-1.html">Adventure Movies</a>
     */
    ADVENTURE_MOVIE(27),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-28-pg-1.html">Thriller Movies</a>
     */
    THRILLER_MOVIE(28),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-29-pg-1.html">Fantasy Movies</a>
     */
    FANTASY_MOVIE(29),
    /**
     * @see <a href="https://www.993vod.com/vod-type-id-30-pg-1.html">Documentaries</a>
     */
    DOCUMENTARY(30);

    private final int id;

    MovieHeavenType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int getCode() {
        return id;
    }

    public boolean isMovie() {
        return (5 <= id && id <= 15) || id == 22;
    }
}
