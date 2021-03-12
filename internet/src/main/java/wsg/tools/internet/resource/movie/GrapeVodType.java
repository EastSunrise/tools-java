package wsg.tools.internet.resource.movie;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;

/**
 * The subtype that an item belongs to in the {@link GrapeSite}.
 *
 * @author Kingen
 * @since 2021/3/4
 */
public enum GrapeVodType implements IntCodeSupplier, TextSupplier {

    /**
     * @see <a href="https://www.putaoys.com/vod/list/dongman/">Animes</a>
     */
    ANIME(3, "dongman"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/zongyi/">Varieties</a>
     */
    VARIETY(4, "zongyi"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/btdy/">BT Movies</a>
     */
    BT_MOVIE(7, "btdy"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/dongzuopian/">Action Movies</a>
     */
    ACTION_MOVIE(8, "dongzuopian"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/xijupian/">Comedy Movies</a>
     */
    COMEDY_MOVIE(9, "xijupian"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/aiqingpian/">Romance Movies</a>
     */
    ROMANCE_MOVIE(10, "aiqingpian"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/kehuanpian/">Science Fiction Movies</a>
     */
    SCI_FI_MOVIE(11, "kehuanpian"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/kongbupian/">Horror Movies</a>
     */
    HORROR_MOVIE(12, "kongbupian"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/zhanzhengpian/">War Movies</a>
     */
    WAR_MOVIE(13, "zhanzhengpian"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/guochanju/">Mainland Series</a>
     */
    MAINLAND_SERIES(15, "guochanju"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/oumeiju/">Western Series</a>
     */
    WEST_SERIES(17, "oumeiju"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/ribenju/">Japanese Series</a>
     */
    JAPAN_SERIES(18, "ribenju"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/haiwaiju/">Overseas Series</a>
     */
    OVERSEAS_SERIES(19, "haiwaiju"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/hanguoju/">Korean Series</a>
     */
    KOREA_SERIES(24, "hanguoju"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/taiguoju/">Tail Series</a>
     */
    THAI_SERIES(25, "taiguoju"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/xianggangju/">Hong Kong Series</a>
     */
    HK_SERIES(27, "xianggangju"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/taiwanju/">Taiwan Series</a>
     */
    TAIWAN_SERIES(28, "taiwanju"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/weidianying/">Online Series</a>
     */
    ONLINE_SERIES(29, "weidianying"),
    /**
     * @see <a href="https://www.putaoys.com/vod/list/juqingpian/">Drama Movies</a>
     */
    DRAMA_MOVIE(31, "juqingpian");

    private final int id;
    private final String text;

    GrapeVodType(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    @Override
    public Integer getCode() {
        return id;
    }
}
