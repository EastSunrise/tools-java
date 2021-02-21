package wsg.tools.boot.common.enums;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;

/**
 * Status when archiving a subject.
 *
 * @author Kingen
 * @since 2020/10/9
 */
public enum VideoStatus implements IntCodeSupplier, TextSupplier {

    ARCHIVED(20, "Archived"),
    COMING(21, "Coming"),
    TO_DOWNLOAD(30, "To download"),
    DOWNLOADING(31, "Downloading"),
    TO_CHOOSE(32, "To choose"),
    LACKING(33, "Lacking"),
    TO_ARCHIVE(40, "To archive");

    private final int code;
    private final String text;

    VideoStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
