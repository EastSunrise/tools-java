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
public interface Downloader {

    /**
     * Downloads the given url.
     *
     * @param dir target directory
     * @param url the url which points to the file to download
     * @return downloaded file
     * @throws IOException if an IO error occurs during downloading
     */
    File download(File dir, URL url) throws IOException;

    /**
     * Downloads the given url with a specific basename.
     *
     * @param dir      target directory
     * @param url      the url which points to the file to download
     * @param basename assign the basename of the file to download
     * @return downloaded file
     * @throws IllegalArgumentException if basename is empty
     * @throws IOException              if an IO error occurs during downloading
     */
    File download(File dir, URL url, String basename) throws IOException;
}
