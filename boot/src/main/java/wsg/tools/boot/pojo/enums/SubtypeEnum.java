package wsg.tools.boot.pojo.enums;

import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.common.jackson.intf.TitleSerializable;

/**
 * Subtype of the subject
 *
 * @author Kingen
 * @since 2020/6/23
 */
public enum SubtypeEnum implements CodeSerializable<Integer>, TitleSerializable {
    /**
     * Movie
     */
    MOVIE(0, "电影"),
    /**
     * TV series
     */
    SERIES(1, "电视剧"),
    /**
     * TV episode
     */
    EPISODE(2, "剧集");

    private int value;
    private String title;

    SubtypeEnum(int value, String title) {
        this.value = value;
        this.title = title;
    }

    /**
     * Transfer
     */
    public static SubtypeEnum of(wsg.tools.internet.video.enums.SubtypeEnum subtype) {
        if (subtype == null) {
            return null;
        }
        if (subtype == wsg.tools.internet.video.enums.SubtypeEnum.MOVIE) {
            return MOVIE;
        } else if (subtype == wsg.tools.internet.video.enums.SubtypeEnum.TV) {
            return SERIES;
        } else if (subtype == wsg.tools.internet.video.enums.SubtypeEnum.TV_SERIES) {
            return SERIES;
        } else if (subtype == wsg.tools.internet.video.enums.SubtypeEnum.TV_EPISODE) {
            return EPISODE;
        }
        return null;
    }

    @Override
    public Integer getCode() {
        return value;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
