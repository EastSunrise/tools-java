package wsg.tools.boot.pojo.enums;

import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.common.jackson.intf.TitleSerializable;
import wsg.tools.internet.video.enums.RecordEnum;

/**
 * Enum of marking type.
 *
 * @author Kingen
 * @since 2020/6/23
 */
public enum MarkEnum implements TitleSerializable, CodeSerializable<Integer> {
    /**
     * Unmarked/wish/do/collect
     */
    UNMARKED(0, "未标记"),
    WISH(1, "想看"),
    DO(2, "在看"),
    COLLECT(3, "看过");

    private int code;
    private String title;

    MarkEnum(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public static MarkEnum of(RecordEnum record) {
        if (record != null) {
            return MarkEnum.valueOf(record.name());
        }
        return UNMARKED;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
