package wsg.tools.boot.pojo.enums;

import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;

/**
 * Status when archiving a subject.
 *
 * @author Kingen
 * @since 2020/10/9
 */
public class VideoStatus implements TextSupplier, CodeSupplier<Integer> {

    public static final VideoStatus NONE_FOUND = new VideoStatus(40, "None found");
    public static final VideoStatus TO_DOWNLOAD = new VideoStatus(30, "To download");
    public static final VideoStatus DOWNLOADING = new VideoStatus(31, "Downloading");
    public static final VideoStatus NONE_DOWNLOADED = new VideoStatus(41, "None downloaded");
    public static final VideoStatus TO_ARCHIVE = new VideoStatus(32, "To archive");

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
