package wsg.tools.internet.download;

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
     * @throws IllegalArgumentException if can't construct a filename by the given url
     * @throws IOException              if an IO error occurs during downloading
     */
    File download(File dir, URL url) throws IOException;

    /**
     * Downloads the given url with a specific basename. If the given basename is null or blank, the
     * source name will be applied.
     *
     * @param dir      target directory
     * @param url      the url which points to the file to download
     * @param basename assign the basename of the file to download
     * @return downloaded file
     * @throws IllegalArgumentException if can't construct a filename by the given arguments
     * @throws IOException              if an IO error occurs during downloading
     */
    File download(File dir, URL url, String basename) throws IOException;
}
