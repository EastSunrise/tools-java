package wsg.tools.boot.entity.subject.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import wsg.tools.common.jackson.intf.TitleSerializable;

/**
 * Status of tag
 *
 * @author Kingen
 * @since 2020/6/23
 */
public enum StatusEnum implements TitleSerializable, IEnum<Integer> {
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

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
