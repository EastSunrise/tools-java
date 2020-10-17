package wsg.tools.boot.pojo.enums;

/**
 * Status when archiving a subject.
 *
 * @author Kingen
 * @since 2020/10/9
 */
public final class ArchivedStatus {

    public static final ArchivedStatus NONE_FOUND = new ArchivedStatus("Not found");
    public static final ArchivedStatus ADDED = new ArchivedStatus("Added");
    public static final ArchivedStatus DOWNLOADING = new ArchivedStatus("Downloading");
    public static final ArchivedStatus NONE_DOWNLOADED = new ArchivedStatus("None downloaded");
    public static final ArchivedStatus ARCHIVED = new ArchivedStatus("Archived");

    private final String key;
    private final String msg;

    private ArchivedStatus(String key) {
        this.key = key;
        this.msg = null;
    }

    private ArchivedStatus(String key, String msg) {
        this.key = key;
        this.msg = msg;
    }

    public static ArchivedStatus noQualified(String msg) {
        return new ArchivedStatus("No qualified", msg);
    }

    public static ArchivedStatus lacking(String msg) {
        return new ArchivedStatus("Lacking", msg);
    }

    @Override
    public String toString() {
        return key + (msg == null ? "" : ("{" + msg + '}'));
    }
}
