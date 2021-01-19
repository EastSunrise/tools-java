package wsg.tools.common.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * Run 32-bits dll applications on Windows.
 *
 * @author Kingen
 * @since 2020/12/2
 */
public class Rundll32 extends CommandExecutor {

    public static final Rundll32 INSTANCE = new Rundll32();

    private Rundll32() {
        super("rundll32");
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
        return INSTANCE.execute("url.dll", "FileProtocolHandler", path);
    }
}
