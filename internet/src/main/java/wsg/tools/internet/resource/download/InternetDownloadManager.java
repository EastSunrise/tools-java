package wsg.tools.internet.resource.download;

import org.apache.commons.io.FilenameUtils;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.SystemUtils;
import wsg.tools.internet.resource.entity.resource.valid.HttpResource;
import wsg.tools.internet.resource.entity.resource.valid.PanResource;
import wsg.tools.internet.resource.entity.resource.valid.YunResource;

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
public class InternetDownloadManager implements Downloader<HttpResource> {

    private final String idman;
    private boolean quitAfter;
    private boolean hangupAfter;
    private boolean silent;
    private boolean manual;
    private boolean ignored;

    public InternetDownloadManager(String idman) {
        try {
            SystemUtils.execute(idman);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        this.idman = idman;
    }

    /**
     * Start queue in scheduler.
     */
    public void startQueue() throws IOException {
        Process process = SystemUtils.execute(idman, "/s");
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * If destination file exists and {@link #ignored} is false when downloading silently,
     * the file will be downloaded with filename appended a number to.
     */
    @Override
    public boolean addTask(File dir, HttpResource resource) throws IOException {
        if (resource instanceof PanResource || resource instanceof YunResource) {
            return false;
        }

        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new SecurityException("Can't create dir " + dir.getPath());
        }

        String filename = resource.filename();
        File dest = new File(dir, filename);
        if (dest.isFile()) {
            if (ignored) {
                return true;
            }

            int count = 1;
            String baseName = FilenameUtils.getBaseName(filename);
            String extension = FilenameUtils.getExtension(filename);
            if (!"".equals(extension)) {
                extension = FilenameUtils.EXTENSION_SEPARATOR + extension;
            }
            do {
                count++;
                dest = new File(dir, baseName + "_" + count + extension);
            } while (dest.isFile());
        }

        List<String> args = new LinkedList<>();
        args.add("/d");
        args.add(resource.getUrl());
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
        Process process = SystemUtils.execute(idman, args.toArray(new String[0]));
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw AssertUtils.runtimeException(e);
        }
        return process.exitValue() == 0;
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
    public InternetDownloadManager ignored(boolean ignored) {
        this.ignored = ignored;
        return this;
    }
}
