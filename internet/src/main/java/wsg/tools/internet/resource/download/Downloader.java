package wsg.tools.internet.resource.download;

import wsg.tools.internet.resource.entity.resource.base.Resource;

import java.io.File;
import java.io.IOException;

/**
 * Utility for downloading.
 *
 * @param <R> type of resources that the downloader can download.
 * @author Kingen
 * @since 2020/9/14
 */
public interface Downloader<R extends Resource> {

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
