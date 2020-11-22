package wsg.tools.boot.pojo.enums;

import lombok.Getter;

import java.io.File;

/**
 * {@link VideoStatus} if archived, including an argument of the target file.
 *
 * @author Kingen
 * @since 2020/11/21
 */
public class VideoArchivedStatus extends VideoStatus {

    public static final int ARCHIVED_CODE = 20;

    @Getter
    private final File file;

    protected VideoArchivedStatus(File file) {
        super(ARCHIVED_CODE, "Archived");
        this.file = file;
    }

    public static VideoStatus archived(File file) {
        return new VideoArchivedStatus(file);
    }
}
