package wsg.tools.internet.resource.site;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.internet.resource.item.VideoType;

/**
 * Types of the resources under {@link XlmSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public enum XlmType implements IntCodeSupplier {
    MAINLAND(1, VideoType.MOVIE, 2),
    HK_TW(2, VideoType.MOVIE, 86),
    WEST(3, VideoType.MOVIE, 3),
    JP_KR(4, VideoType.MOVIE, 10),
    MAINLAND_SERIES(5, VideoType.SERIES, 27),
    HK_TW_SERIES(6, VideoType.SERIES, 56),
    JP_KR_SERIES(7, VideoType.SERIES, 46),
    WEST_SERIES(8, VideoType.SERIES, 23),
    VARIETY(9, VideoType.VARIETY, 33),
    ANIME(10, VideoType.ANIME, 7),
    THREE_D(11, VideoType.THREE_D, 1),
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

    public int getFirst() {
        return first;
    }

    public VideoType getVideoType() {
        return videoType;
    }
}
