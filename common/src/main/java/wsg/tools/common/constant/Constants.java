package wsg.tools.common.constant;

import java.time.format.DateTimeFormatter;

/**
 * Constant variables
 *
 * @author Kingen
 * @since 2020/6/24
 */
public class Constants {

    public static final String NUMBER_DELIMITER = ",";

    public static final String NULL_NA = "N/A";

    public static final String STANDARD_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter STANDARD_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(STANDARD_DATE_TIME_PATTERN);
    public static final DateTimeFormatter STANDARD_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
}
