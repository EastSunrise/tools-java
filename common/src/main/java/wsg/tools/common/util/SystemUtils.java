package wsg.tools.common.util;

import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

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

    /**
     * Test performance of a method
     *
     * @param times    times to execute
     * @param consumer executing method
     * @return duration of execution
     */
    public static <T> long runtime(long times, Consumer<T> consumer) {
        Validate.isTrue(times > 0);
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            consumer.accept(null);
        }
        return System.currentTimeMillis() - start;
    }
}
