package wsg.tools.internet.resource.download;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.impl.HttpResource;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Basic downloader to download a resource directly.
 *
 * @author Kingen
 * @since 2021/2/22
 */
@Slf4j
public class BasicDownloader implements Downloader<HttpResource> {

    private static BasicDownloader instance;

    private final CloseableHttpClient client;
    private FileExistStrategy strategy = FileExistStrategy.RENAME;

    private BasicDownloader() {
        this.client = HttpClientBuilder.create().build();
    }

    public static BasicDownloader getInstance() {
        if (instance == null) {
            instance = new BasicDownloader();
        }
        return instance;
    }

    public boolean download(File dir, String url, @Nonnull String basename) throws IOException, InvalidResourceException {
        HttpResource resource = HttpResource.of(null, url);
        String filename = basename + FilenameUtils.EXTENSION_SEPARATOR + FilenameUtils.getExtension(resource.getFilename());
        return execute(dir, url, filename);
    }

    @Override
    public boolean addTask(File dir, HttpResource resource) throws IOException {
        return execute(dir, resource.getUrl(), resource.getFilename());
    }

    private boolean execute(File dir, String url, String filename) throws IOException {
        File destination = Downloader.destination(dir, filename, strategy);
        if (destination != null) {
            log.info("Get from {}", url);
            return client.execute(new HttpGet(url), new FileHandler(destination));
        }
        return true;
    }

    public BasicDownloader strategy(@Nonnull FileExistStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public FileExistStrategy getStrategy() {
        return strategy;
    }

    static class FileHandler extends AbstractResponseHandler<Boolean> {

        private final File dest;

        FileHandler(@Nonnull File dest) {this.dest = dest;}

        @Override
        public Boolean handleEntity(HttpEntity entity) throws IOException {
            InputStream inputStream = entity.getContent();
            if (dest.isFile()) {
                throw new IllegalArgumentException("Target file exists: " + dest.getPath());
            }
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                byte[] data = new byte[1024];
                int len;
                while ((len = inputStream.read(data)) != -1) {
                    fos.write(data, 0, len);
                }
            }
            return true;
        }
    }
}
