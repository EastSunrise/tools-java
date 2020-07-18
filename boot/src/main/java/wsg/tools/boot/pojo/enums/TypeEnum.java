package wsg.tools.boot.pojo.enums;

import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.internet.video.enums.ImdbTypeEnum;
import wsg.tools.internet.video.enums.SubtypeEnum;

/**
 * Type of a subject.
 *
 * @author Kingen
 * @since 2020/7/18
 */
public enum TypeEnum implements CodeSerializable<Integer> {
    /**
     * Movie/Series/Season
     */
    MOVIE(0),
    SERIES(1),
    SEASON(2);

    private int code;

    TypeEnum(int code) {
        this.code = code;
    }

    public static TypeEnum of(SubtypeEnum subtype) {
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

    public static TypeEnum of(ImdbTypeEnum imdbType) {
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
}
