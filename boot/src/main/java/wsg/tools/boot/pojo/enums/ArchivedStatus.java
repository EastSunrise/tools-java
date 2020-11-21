package wsg.tools.boot.pojo.enums;

import wsg.tools.common.util.function.TextSupplier;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * Status when archiving a subject.
 *
 * @author Kingen
 * @since 2020/10/9
 */
public final class ArchivedStatus implements TextSupplier {

    /**
     * Status when archiving resources
     */
    public static final ArchivedStatus NONE_FOUND = new ArchivedStatus("None found");
    public static final ArchivedStatus TO_DOWNLOAD = new ArchivedStatus("To download");
    public static final ArchivedStatus DOWNLOADING = new ArchivedStatus("Downloading");
    public static final ArchivedStatus NONE_DOWNLOADED = new ArchivedStatus("None downloaded");

    private final String status;
    private File file;

    private ArchivedStatus(String status) {
        this.status = status;
    }

    private ArchivedStatus(String status, @Nonnull File file) {
        this.status = status;
        this.file = file;
    }

    public static ArchivedStatus archived(File file) {
        return new ArchivedStatus("Archived", file);
    }

    public static ArchivedStatus toArchive(File file) {
        return new ArchivedStatus("To archive", file);
    }

    public boolean archived() {
        return "Archived".equals(status);
    }

    public boolean toArchive() {
        return "To archive".equals(status);
    }

    public String getStatus() {
        return status;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getText() {
        return status;
    }
}
