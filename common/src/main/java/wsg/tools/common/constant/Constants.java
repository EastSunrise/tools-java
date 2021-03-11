package wsg.tools.common.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Common constant variables
 *
 * @author Kingen
 * @since 2020/6/24
 */
public final class Constants {

    /**
     * Common strings.
     */
    public static final String SYSTEM_TMPDIR = System.getProperty("java.io.tmpdir");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILE_EXTENSION_SEPARATOR = ".";
    public static final String URL_PATH_SEPARATOR = "/";
    public static final String URL_SCHEME_SEPARATOR = ":";
    public static final String EMPTY_STRING = "";
    public static final String WHITESPACE = " ";
    /**
     * Common arguments.
     */
    public static final int DEFAULT_MAP_CAPACITY = 16;
    public static final int KILOBYTE = 1024;
    /**
     * Common datetime formatters.
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.CHINESE)
            .withZone(ZoneId.systemDefault());
    /**
     * Common charsets.
     */
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    public static final Charset GBK = Charset.forName("GBK");

    private Constants() {
    }

    /**
     * Common functions.
     */
    public static <T> Consumer<T> emptyConsumer() {
        return t -> {
        };
    }
}
