package wsg.tools.internet.download.impl;

import wsg.tools.common.io.CommandExecutor;
import wsg.tools.internet.download.FileExistStrategy;
import wsg.tools.internet.download.base.Downloader;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Downloader of IDM.
 *
 * @author Kingen
 * @see <a href="http://www.internetdownloadmanager.com/">IDM</a>
 * @since 2020/11/5
 */
public class InternetDownloadManager extends CommandExecutor implements Downloader<HttpLink> {

    private boolean quitAfter;
    private boolean hangupAfter;
    private boolean silent;
    private boolean manual;
    private FileExistStrategy strategy = FileExistStrategy.RENAME;

    public InternetDownloadManager(String idman) {
        super(idman);
    }

    /**
     * Start queue in scheduler.
     */
    public void startQueue() throws IOException {
        execute("/s");
    }

    @Override
    public boolean addTask(File dir, HttpLink link) throws IOException {
        File dest = Downloader.destination(dir, link.getFilename(), strategy);
        if (dest == null) {
            return true;
        }

        List<String> args = new LinkedList<>();
        args.add("/d");
        args.add(link.getUrl());
        args.add("/p");
        args.add(dir.getAbsolutePath());
        args.add("/f");
        args.add(dest.getName());
        if (quitAfter) {
            args.add("/q");
        }
        if (hangupAfter) {
            args.add("/h");
        }
        if (silent) {
            args.add("/n");
        }
        if (manual) {
            args.add("/a");
        }
        return execute(args.toArray(new String[0])) == 0;
    }

    /**
     * Whether to exit after the successful downloading. This parameter works only for the first copy.
     */
    public InternetDownloadManager quitAfter(boolean quitAfter) {
        this.quitAfter = quitAfter;
        return this;
    }

    /**
     * Whether to hang up connection after the successful downloading
     */
    public InternetDownloadManager hangupAfter(boolean hangupAfter) {
        this.hangupAfter = hangupAfter;
        return this;
    }

    /**
     * Whether to turn on the silent mode when IDM doesn't ask any questions.
     */
    public InternetDownloadManager silent(boolean silent) {
        this.silent = silent;
        return this;
    }

    /**
     * Whether to start downloading manually after adding a file specified with /d to download queue.
     */
    public InternetDownloadManager manual(boolean manual) {
        this.manual = manual;
        return this;
    }

    /**
     * Whether to ignore the task when a file with the same filename already exists under the target directory.
     * If not, rename file of the task with a number appended to.
     */
    public InternetDownloadManager strategy(@Nonnull FileExistStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
}
