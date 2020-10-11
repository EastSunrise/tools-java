package wsg.tools.boot.pojo.enums;

import lombok.Setter;

/**
 * Status when archiving a subject.
 *
 * @author Kingen
 * @since 2020/10/9
 */
public enum ArchivedStatus {
    /**
     * Statuses of archiving
     */
    NONE_FOUND,
    ADDED,
    DOWNLOADING,
    NONE_DOWNLOADED,
    LACKING,
    NO_QUALIFIED,
    ARCHIVED;

    @Setter
    private String msg;

    @Override
    public String toString() {
        return name() + (msg == null ? "" : ("{" + msg + '}'));
    }
}
