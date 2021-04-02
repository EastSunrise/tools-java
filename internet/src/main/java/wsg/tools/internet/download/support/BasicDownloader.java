package wsg.tools.internet.download.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.io.FileUtilExt;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.download.Downloader;
import wsg.tools.internet.download.FileExistStrategy;

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
    public File download(File dir, @Nonnull URL url) throws IOException {
        return execute(dir, url, StringUtilsExt.toFilename(FilenameUtils.getName(url.getFile())));
    }

    /**
     * Downloads the given url and names the downloaded file with the given basename.
     */
    @Override
    public File download(File dir, @Nonnull URL url, String basename) throws IOException {
        if (StringUtils.isBlank(basename)) {
            return download(dir, url);
        }
        String filename = FileUtilExt.copyExtension(url.getFile(), basename);
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
        try {
            FileUtils.copyURLToFile(url, dest, TIMEOUT, TIMEOUT);
        } catch (IOException e) {
            Matcher matcher = Lazy.RESPONSE_EXCEPTION_REGEX.matcher(e.getMessage());
            if (matcher.lookingAt()) {
                int code = Integer.parseInt(matcher.group("c"));
                throw new OtherResponseException(code, e.getMessage());
            }
            throw e;
        }
        return dest;
    }

    @Nonnull
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

    private static class Lazy {

        private static final Pattern RESPONSE_EXCEPTION_REGEX = Pattern
            .compile("Server returned HTTP response code: (?<c>[45]\\d{2})");
    }
}
