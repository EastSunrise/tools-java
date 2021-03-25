package wsg.tools.internet.download.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.download.FileExistStrategy;
import wsg.tools.internet.download.base.Downloader;

/**
 * A basic downloader to download a link directly.
 *
 * @author Kingen
 * @since 2021/2/22
 */
@Slf4j
public final class BasicDownloader implements Downloader {

    private static final int TIMEOUT = 15000;
    private FileExistStrategy strategy = FileExistStrategy.RENAME;

    public BasicDownloader() {
    }

    @Override
    public File download(File dir, URL url) throws IOException {
        String file = StringUtilsExt.toFilename(StringUtils.stripEnd(url.getFile(), "/"));
        return execute(dir, url, FilenameUtils.getName(file));
    }

    /**
     * Downloads the given url and names the downloaded file with the given basename.
     */
    @Override
    public File download(File dir, URL url, @Nonnull String basename)
        throws IOException {
        String file = StringUtilsExt.toFilename(StringUtils.stripEnd(url.getFile(), "/"));
        String extension = FilenameUtils.getExtension(file);
        if (!extension.isEmpty()) {
            extension = FilenameUtils.EXTENSION_SEPARATOR + extension;
        }
        String filename = basename + extension;
        return execute(dir, url, filename);
    }

    /**
     * Downloads the target file based on the given directory and filename.
     *
     * @param dir      target directory
     * @param filename filename
     * @return the downloaded file
     */
    private File execute(File dir, URL url, String filename) throws IOException {
        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("Empty filename");
        }
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new SecurityException("Can't create dir " + dir.getPath());
        }

        File dest = new File(dir, filename);
        if (dest.exists()) {
            if (dest.isDirectory()) {
                // rename if the destination is a directory
                dest = rename(dir, filename);
            } else {
                if (FileExistStrategy.FINISH == strategy) {
                    return dest;
                }
                if (FileExistStrategy.RENAME == strategy) {
                    dest = rename(dir, filename);
                }
            }
        }
        log.info("Downloading from {} to {}", url, dest);
        FileUtils.copyURLToFile(url, dest, TIMEOUT, TIMEOUT);
        return dest;
    }

    private File rename(File dir, String filename) {
        int count = 1;
        String baseName = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);
        if (!extension.isEmpty()) {
            extension = FilenameUtils.EXTENSION_SEPARATOR + extension;
        }
        while (true) {
            count++;
            File dest = new File(dir, baseName + "_" + count + extension);
            if (!dest.exists()) {
                return dest;
            }
        }
    }

    public BasicDownloader strategy(@Nonnull FileExistStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public FileExistStrategy getStrategy() {
        return strategy;
    }
}
