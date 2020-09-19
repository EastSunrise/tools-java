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
    public static Process openFile(File file) throws IOException {
        Objects.requireNonNull(file, "Can't open a null file.");
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath());
        }

        return open(file.getAbsolutePath());
    }

    /**
     * Open a url with local default browser or corresponding application.
     *
     * @param url     url string
     * @param urlArgs optional args to format the url
     */
    public static Process openUrl(String url, Object... urlArgs) throws IOException {
        if (urlArgs.length == 0) {
            return open(url);
        }
        return open(String.format(url, urlArgs));
    }

    /**
     * Open the given target. Only useful for Windows.
     *
     * @param target a url or a path referring to a file
     */
    private static Process open(String target) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        return runtime.exec("rundll32 url.dll FileProtocolHandler " + target);
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
