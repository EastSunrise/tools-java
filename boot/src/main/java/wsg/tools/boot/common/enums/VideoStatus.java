package wsg.tools.boot.common.enums;

import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;

/**
 * Status when archiving a subject.
 *
 * @author Kingen
 * @since 2020/10/9
 */
public class VideoStatus implements TextSupplier, CodeSupplier<Integer> {

    public static final VideoStatus ARCHIVED = new VideoStatus(20, "Archived");
    public static final VideoStatus COMING = new VideoStatus(21, "Coming");
    public static final VideoStatus TO_DOWNLOAD = new VideoStatus(30, "To download");
    public static final VideoStatus DOWNLOADING = new VideoStatus(31, "Downloading");
    public static final VideoStatus TO_CHOOSE = new VideoStatus(32, "To choose");
    public static final VideoStatus LACKING = new VideoStatus(33, "Lacking");
    public static final VideoStatus TO_ARCHIVE = new VideoStatus(40, "To archive");

    private final int code;
    private final String text;

    protected VideoStatus(int code, String text) {
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
