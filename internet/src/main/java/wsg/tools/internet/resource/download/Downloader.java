package wsg.tools.internet.resource.download;

import wsg.tools.internet.resource.entity.resource.AbstractResource;

import java.io.File;
import java.io.IOException;

/**
 * Utility for downloading.
 *
 * @author Kingen
 * @since 2020/9/14
 */
public interface Downloader {

    /**
     * Download the given resource.
     *
     * @param dir      target directory
     * @param resource resource to download
     * @throws IOException i/o exception
     */
    void download(File dir, AbstractResource resource) throws IOException;
}
