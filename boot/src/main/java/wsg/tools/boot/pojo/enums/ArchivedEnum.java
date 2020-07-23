package wsg.tools.boot.pojo.enums;

import wsg.tools.common.function.CodeSupplier;
import wsg.tools.common.function.TitleSupplier;

/**
 * Archived status of the subject
 *
 * @author Kingen
 * @since 2020/6/23
 */
public enum ArchivedEnum implements CodeSupplier<Integer>, TitleSupplier {
    /**
     * Archived status
     */
    NONE(0, "没有资源"),
    ADDED(1, "已添加"),
    PLAYABLE(2, "可播放"),
    DOWNLOADING(3, "下载中");

    private final int code;
    private final String title;

    ArchivedEnum(int code, String title) {
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
}
