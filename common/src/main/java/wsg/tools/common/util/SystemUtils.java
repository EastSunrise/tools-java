package wsg.tools.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * Utility for system operations.
 *
 * @author Kingen
 * @since 2020/6/29
 */
public class SystemUtils {

    /**
     * Open a file with local default application.
     */
    public static void openFile(File file) throws IOException {
        Objects.requireNonNull(file);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath());
        }

        Runtime runtime = Runtime.getRuntime();
        runtime.exec("rundll32 url.dll FileProtocolHandler " + file.getAbsolutePath());
    }
}
