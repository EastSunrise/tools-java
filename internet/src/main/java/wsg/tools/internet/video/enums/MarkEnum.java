package wsg.tools.internet.video.enums;

import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.common.jackson.intf.TitleSerializable;
import wsg.tools.internet.base.PathParameterized;

/**
 * Enum of marking type.
 *
 * @author Kingen
 * @since 2020/6/23
 */
public enum MarkEnum implements TitleSerializable, CodeSerializable<Integer>, PathParameterized {
    /**
     * wish/do/collect
     */
    WISH(1, "想看"),
    DO(2, "在看"),
    COLLECT(3, "看过");

    private int code;
    private String title;

    MarkEnum(int code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getPath() {
        return name().toLowerCase();
    }
}
