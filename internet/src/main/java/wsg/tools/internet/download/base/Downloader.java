package wsg.tools.internet.download.base;

import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import wsg.tools.internet.download.FileExistStrategy;

/**
 * Downloads resources.
 *
 * @param <T> type of links that the downloader can download.
 * @author Kingen
 * @since 2020/9/14
 */
@FunctionalInterface
public interface Downloader<T extends AbstractLink> {

    /**
     * Obtains the target file based on the given directory and filename.
     *
     * @param dir      target directory
     * @param filename filename
     * @param strategy strategy when the target file exists
     * @return the target file, null if strategy is {@link FileExistStrategy#FINISH} and the target
     * exists
     */
    @Nullable
    static File destination(File dir, String filename, FileExistStrategy strategy) {
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new SecurityException("Can't create dir " + dir.getPath());
        }

        File dest = new File(dir, filename);
        if (dest.isFile()) {
            if (FileExistStrategy.FINISH == strategy) {
                return null;
            }

            if (FileExistStrategy.RENAME == strategy) {
                int count = 1;
                String baseName = FilenameUtils.getBaseName(filename);
                String extension = FilenameUtils.getExtension(filename);
                if (!"".equals(extension)) {
                    extension = FilenameUtils.EXTENSION_SEPARATOR + extension;
                }
                do {
                    count++;
                    dest = new File(dir, baseName + "_" + count + extension);
                } while (dest.isFile());
            }
        }
        return dest;
    }

    /**
     * Download the given link.
     *
     * @param dir  target directory
     * @param link link to download
     * @return result of adding
     * @throws IOException i/o exception
     */
    boolean addTask(File dir, T link) throws IOException;
}
