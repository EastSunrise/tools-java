package wsg.tools.internet.download.base;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Downloads a resource.
 *
 * @author Kingen
 * @since 2020/9/14
 */
@FunctionalInterface
public interface Downloader {

    /**
     * Downloads the given url.
     *
     * @param dir target directory
     * @param url the url which points to the file to download
     * @return downloaded file
     * @throws IOException i/o exception
     */
    File download(File dir, URL url) throws IOException;
}
