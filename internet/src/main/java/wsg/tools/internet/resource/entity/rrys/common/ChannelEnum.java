package wsg.tools.internet.resource.entity.rrys.common;

import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.site.RrysSite;

/**
 * Channels of resources of {@link RrysSite}.
 *
 * @author Kingen
 * @since 2020/12/24
 */
public enum ChannelEnum {

    MOVIE,
    TV,
    OPENCLASS;

    public VideoType videoType() {
        switch (this) {
            case MOVIE:
                return VideoType.MOVIE;
            case TV:
                return VideoType.TV;
            case OPENCLASS:
                return VideoType.COURSE;
            default:
                throw new IllegalArgumentException("Unknown channel '" + this + "'");
        }
    }
}
