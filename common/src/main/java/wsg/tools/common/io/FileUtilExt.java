package wsg.tools.common.io;

import javax.annotation.Nonnull;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import wsg.tools.common.constant.Constants;

/**
 * Extension of {@link FileUtils} and {@link FilenameUtils}.
 *
 * @author Kingen
 * @since 2021/3/28
 */
public final class FileUtilExt {

    private FileUtilExt() {
    }

    /**
     * Copies the extension of the filename to append to the given basename. If the basename is
     * null, the source filename will be returned.
     *
     * @param filename the source filename
     * @param basename the basename to which the extension is appended
     * @return a new filename based on the basename and ending with the same extension or the source
     * filename if the given basename is null
     */
    @Nonnull
    public static String copyExtension(@Nonnull String filename, String basename) {
        if (basename == null) {
            return filename;
        }
        String extension = FilenameUtils.getExtension(filename);
        if (!extension.isEmpty()) {
            extension = Constants.EXTENSION_SEPARATOR + extension;
        }
        return basename + extension;
    }
}
