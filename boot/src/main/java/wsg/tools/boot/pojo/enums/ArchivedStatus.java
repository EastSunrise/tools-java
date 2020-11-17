package wsg.tools.boot.pojo.enums;

import lombok.Getter;

/**
 * Status when archiving a subject.
 *
 * @author Kingen
 * @since 2020/10/9
 */
@Getter
public final class ArchivedStatus {

    public static final ArchivedStatus EXISTED = new ArchivedStatus("Existed");
    public static final ArchivedStatus NONE_FOUND = new ArchivedStatus("None found");
    public static final ArchivedStatus ADDED = new ArchivedStatus("Added");
    public static final ArchivedStatus DOWNLOADING = new ArchivedStatus("Downloading");
    public static final ArchivedStatus NONE_DOWNLOADED = new ArchivedStatus("None downloaded");
    public static final ArchivedStatus ARCHIVED = new ArchivedStatus("Archived");

    private final String status;
    private final String msg;

    private ArchivedStatus(String status) {
        this.status = status;
        this.msg = status;
    }

    private ArchivedStatus(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    /**
     * If none of downloaded resources are qualified.
     */
    public static ArchivedStatus noneQualified(String msg) {
        return new ArchivedStatus("None qualified", msg);
    }

    /**
     * If downloaded resources contain any unknown resource.
     */
    public static ArchivedStatus unknown(String msg) {
        return new ArchivedStatus("Unknown", msg);
    }

    public static ArchivedStatus lacking(String msg) {
        return new ArchivedStatus("Lacking", msg);
    }

    @Override
    public String toString() {
        return status + (msg.equals(status) ? "" : ("{" + msg + '}'));
    }
}
