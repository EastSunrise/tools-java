package wsg.tools.boot.pojo.enums;

import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.common.jackson.intf.TitleSerializable;
import wsg.tools.internet.video.enums.RecordEnum;

/**
 * Status of tag
 *
 * @author Kingen
 * @since 2020/6/23
 */
public enum StatusEnum implements TitleSerializable, CodeSerializable<Integer> {
    /**
     * Unmarked/wish/do/collect
     */
    UNMARKED(0, "未标记"),
    WISH(1, "想看"),
    DO(2, "在看"),
    COLLECT(3, "看过");

    private int code;
    private String title;

    StatusEnum(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public static StatusEnum of(RecordEnum record) {
        if (record != null) {
            switch (record) {
                case WISH:
                    return WISH;
                case DO:
                    return DO;
                case COLLECT:
                    return COLLECT;
                default:
                    break;
            }
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
