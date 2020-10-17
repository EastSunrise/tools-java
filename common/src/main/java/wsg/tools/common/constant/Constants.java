package wsg.tools.common.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

/**
 * Constant variables
 *
 * @author Kingen
 * @since 2020/6/24
 */
public class Constants {

    public static final String NULL_NA = "N/A";

    public static final int DEFAULT_MAP_CAPACITY = 16;

    public static final String STANDARD_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter STANDARD_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(STANDARD_DATE_TIME_PATTERN).withLocale(Locale.CHINESE).withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter NULL_FORMATTER = new DateTimeFormatterBuilder().toFormatter();

    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    public static final Charset GBK = Charset.forName("GBK");

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
}
