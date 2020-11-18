package wsg.tools.boot.pojo.enums;

import wsg.tools.common.util.function.TextSupplier;

/**
 * Status when archiving a subject.
 *
 * @author Kingen
 * @since 2020/10/9
 */
public enum ArchivedStatus implements TextSupplier {

    /**
     * Status when archiving resources
     */
    ARCHIVED("Archived"),
    NONE_FOUND("None found"),
    TO_DOWNLOAD("To download"),
    DOWNLOADING("Downloading"),
    NONE_DOWNLOADED("None downloaded"),
    TO_ARCHIVE("To archive");

    private final String status;

    ArchivedStatus(String status) {
        this.status = status;
    }

    @Override
    public String getText() {
        return status;
    }
}
