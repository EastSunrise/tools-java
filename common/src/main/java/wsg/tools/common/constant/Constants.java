package wsg.tools.common.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.experimental.UtilityClass;

/**
 * Common constant variables
 *
 * @author Kingen
 * @since 2020/6/24
 */
@UtilityClass
public class Constants {

    /**
     * Common strings.
     */
    public final String SYSTEM_TMPDIR = System.getProperty("java.io.tmpdir");

    public final String LINE_SEPARATOR = System.getProperty("line.separator");

    public final String FILE_EXTENSION_SEPARATOR = ".";

    public final String URL_PATH_SEPARATOR = "/";

    public final String URL_SCHEME_SEPARATOR = ":";

    /**
     * Common arguments.
     */
    public final int DEFAULT_MAP_CAPACITY = 16;

    /**
     * Common datetime formatters.
     */
    public final DateTimeFormatter STANDARD_DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.CHINESE)
            .withZone(ZoneId.systemDefault());

    /**
     * Common charsets.
     */
    public final Charset UTF_8 = StandardCharsets.UTF_8;

    public final Charset GBK = Charset.forName("GBK");
}
