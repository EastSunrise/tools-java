package wsg.tools.boot.pojo.enums;

import wsg.tools.common.function.CodeSupplier;
import wsg.tools.common.function.TitleSupplier;
import wsg.tools.internet.video.enums.ImdbTypeEnum;
import wsg.tools.internet.video.enums.SubtypeEnum;

/**
 * Type of a subject.
 *
 * @author Kingen
 * @since 2020/7/18
 */
public enum VideoTypeEnum implements CodeSupplier<Integer>, TitleSupplier {
    /**
     * Movie/Series/Season
     */
    MOVIE(0, "电影"),
    SERIES(1, "剧集"),
    SEASON(2, "单季");

    private final int code;
    private final String title;

    VideoTypeEnum(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public static VideoTypeEnum of(SubtypeEnum subtype) {
        if (subtype == null) {
            return null;
        }
        switch (subtype) {
            case MOVIE:
                return MOVIE;
            case TV:
                return SEASON;
            default:
                throw new IllegalArgumentException("Unknown subtype " + subtype);
        }
    }

    public static VideoTypeEnum of(ImdbTypeEnum imdbType) {
        if (imdbType == null) {
            return null;
        }
        switch (imdbType) {
            case MOVIE:
                return MOVIE;
            case SERIES:
                return SERIES;
            case EPISODE:
                return SEASON;
            default:
                throw new IllegalArgumentException("Unknown imdb type " + imdbType);
        }
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }
}