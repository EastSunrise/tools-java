package wsg.tools.internet.download.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.download.FileExistStrategy;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.base.Downloader;

/**
 * A basic downloader to download a link directly.
 *
 * @author Kingen
 * @since 2021/2/22
 */
@Slf4j
public final class BasicDownloader implements Downloader {

    private final CloseableHttpClient client;
    private FileExistStrategy strategy = FileExistStrategy.RENAME;

    public BasicDownloader() {
        this.client = HttpClientBuilder.create().build();
    }

    @Override
    public File download(File dir, URL url) throws IOException {
        String file = url.getFile();
        String path = FilenameUtils.getPath(file);
        return execute(new File(dir, path), url, FilenameUtils.getName(file));
    }

    /**
     * Downloads the given url and names the downloaded file with the given basename.
     */
    public File download(File dir, URL url, @Nonnull String basename)
        throws IOException, InvalidResourceException {
        String extension = FilenameUtils.getExtension(url.getFile());
        String newName = basename + FilenameUtils.EXTENSION_SEPARATOR + extension;
        return execute(dir, url, newName);
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
        if (dest.isFile()) {
            if (FileExistStrategy.FINISH == strategy) {
                return dest;
            }

            if (FileExistStrategy.RENAME == strategy) {
                int count = 1;
                String baseName = FilenameUtils.getBaseName(filename);
                String extension = FilenameUtils.getExtension(filename);
                if (!extension.isEmpty()) {
                    extension = FilenameUtils.EXTENSION_SEPARATOR + extension;
                }
                do {
                    count++;
                    dest = new File(dir, baseName + "_" + count + extension);
                } while (dest.isFile());
            }
        }
        log.info("Downloading from {} to {}", url, dest);
        return client.execute(new HttpGet(url.toString()), new FileHandler(dest));
    }

    public BasicDownloader strategy(@Nonnull FileExistStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public FileExistStrategy getStrategy() {
        return strategy;
    }

    private static class FileHandler extends AbstractResponseHandler<File> {

        private final File dest;

        FileHandler(@Nonnull File dest) {
            this.dest = dest;
        }

        @Override
        public File handleEntity(HttpEntity entity) throws IOException {
            InputStream inputStream = entity.getContent();
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                byte[] data = new byte[Constants.KILOBYTE];
                int len;
                while ((len = inputStream.read(data)) != -1) {
                    fos.write(data, 0, len);
                }
            }
            return dest;
        }
    }
}
