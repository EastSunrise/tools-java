package wsg.tools.internet.video.enums;

import wsg.tools.internet.base.PathParameterized;

/**
 * Type of status on subject that users mark.
 *
 * @author Kingen
 * @since 2020/6/29
 */
public enum RecordEnum implements PathParameterized {
    /**
     * wish/do/collect
     */
    WISH, DO, COLLECT;

    @Override
    public String getPath() {
        return name().toLowerCase();
    }
}
