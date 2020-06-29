package wsg.tools.boot.entity.subject.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import wsg.tools.common.jackson.intf.TitleSerializable;

/**
 * Archived status of the subject
 *
 * @author Kingen
 * @since 2020/6/23
 */
public enum ArchivedEnum implements IEnum<Integer>, TitleSerializable {
    /**
     * Archived status
     */
    NONE(0, "没有资源"),
    ADDED(1, "已添加"),
    PLAYABLE(2, "可播放"),
    DOWNLOADING(3, "下载中");

    private int code;
    private String title;

    ArchivedEnum(int code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
