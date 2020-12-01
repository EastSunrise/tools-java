package wsg.tools.common.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * Protocol handler of files on Windows.
 *
 * @author Kingen
 * @since 2020/12/2
 */
public class FileProtocolHandler extends CommandExecutor {

    public static final FileProtocolHandler INSTANCE = new FileProtocolHandler();

    private FileProtocolHandler() {
        super("rundll32 url.dll FileProtocolHandler");
    }

    /**
     * Open a file with local default application.
     */
    public static int openFile(File file) throws IOException {
        Objects.requireNonNull(file, "Can't open a null file.");
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath());
        }

        return open(file.getAbsolutePath());
    }

    /**
     * Open a resource of the given path with local default browser or corresponding application.
     *
     * @param path path to the resource
     * @return exit value
     */
    public static int open(String path) throws IOException {
        return INSTANCE.execute(path);
    }
}
