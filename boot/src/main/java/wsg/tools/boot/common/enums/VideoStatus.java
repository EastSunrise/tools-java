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

    /**
     * If archived
     */
    ARCHIVED(20, "Archived"),
    /**
     * If not released
     */
    COMING(21, "Coming"),
    /**
     * If it's to be downloaded
     */
    TO_DOWNLOAD(30, "To download"),
    /**
     * If in downloading
     */
    DOWNLOADING(31,
        "Downloading"),
    /**
     * If to be chosen
     */
    TO_CHOOSE(32, "To choose"),
    /**
     * If lacking of some episodes
     */
    LACKING(33, "Lacking"),
    /**
     * If to be archived
     */
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
