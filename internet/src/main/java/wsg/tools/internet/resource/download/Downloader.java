package wsg.tools.internet.resource.download;

import org.apache.commons.io.FilenameUtils;
import wsg.tools.internet.resource.base.AbstractResource;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * Utility for downloading.
 *
 * @param <R> type of resources that the downloader can download.
 * @author Kingen
 * @since 2020/9/14
 */
public interface Downloader<R extends AbstractResource> {

    /**
     * Obtains the target file based on the given directory and filename.
     *
     * @param dir      target directory
     * @param filename filename
     * @param strategy strategy when the target file exists
     * @return the target file, null if strategy is {@link FileExistStrategy#FINISH} and the target exists
     */
    @Nullable
    static File destination(File dir, String filename, FileExistStrategy strategy) {
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new SecurityException("Can't create dir " + dir.getPath());
        }

        File dest = new File(dir, filename);
        if (dest.isFile()) {
            if (FileExistStrategy.FINISH.equals(strategy)) {
                return null;
            }

            if (FileExistStrategy.RENAME.equals(strategy)) {
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
     * Download the given resource.
     *
     * @param dir      target directory
     * @param resource resource to download
     * @return result of adding
     * @throws IOException i/o exception
     */
    boolean addTask(File dir, R resource) throws IOException;
}
