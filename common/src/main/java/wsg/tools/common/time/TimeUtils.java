package wsg.tools.common.time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

/**
 * Utility for {@link java.time}.
 *
 * @author Kingen
 * @since 2021/3/17
 */
public final class TimeUtils {

    private TimeUtils() {
    }

    /**
     * Parses a text to a duration (accurate to a milliseconds). The minimum is one minutes and the
     * maximum is less than 100 hours.
     * <p>
     * Examples:
     * <pre>
     *     "1:00" -- parses as "1 minutes"
     *     "23:22.1" -- parses as "23 minutes and 22.1 seconds"
     *     "1:30:00" -- parses as "1 hour and 30 minutes"
     * </pre>
     *
     * @param text the text to parse, not null
     * @return the parsed duration, not null
     * @throws DateTimeParseException if the text cannot be parsed to a duration
     */
    public static Duration parseDuration(@Nonnull String text) {
        Matcher matcher = Lazy.DURATION_REGEX.matcher(text);
        if (!matcher.matches()) {
            throw new DateTimeParseException("Text cannot be parsed to a Duration: " + text,
                text, 0);
        }
        Duration duration = Duration.ofMinutes(Long.parseLong(matcher.group("m")))
            .plusSeconds(Long.parseLong(matcher.group("s")));
        String millis = matcher.group("mi");
        if (millis != null) {
            duration = duration.plusMillis(Long.parseLong(millis) * 100);
        }
        String hour = matcher.group("h");
        if (hour == null) {
            return duration;
        }
        return duration.plusHours(Long.parseLong(hour));
    }

    public static LocalDateTime offset(LocalDateTime base, @Nonnull String text) {
        if (text.endsWith(Lazy.MINUTES_AGO)) {
            text = text.substring(0, text.length() - Lazy.MINUTES_AGO.length() - 1);
            return base.minusMinutes(Integer.parseInt(text));
        }
        if (text.endsWith(Lazy.HOURS_AGO)) {
            text = text.substring(0, text.length() - Lazy.HOURS_AGO.length() - 1);
            return base.minusHours(Integer.parseInt(text));
        }
        if (text.endsWith(Lazy.DAY_AGO)) {
            text = text.substring(0, text.length() - Lazy.DAY_AGO.length() - 1);
            return base.minusDays(Integer.parseInt(text));
        }
        if (text.endsWith(Lazy.DAYS_AGO)) {
            text = text.substring(0, text.length() - Lazy.DAYS_AGO.length() - 1);
            return base.minusDays(Integer.parseInt(text));
        }
        if (text.endsWith(Lazy.WEEK_AGO)) {
            text = text.substring(0, text.length() - Lazy.WEEK_AGO.length() - 1);
            return base.minusWeeks(Integer.parseInt(text));
        }
        if (text.endsWith(Lazy.WEEKS_AGO)) {
            text = text.substring(0, text.length() - Lazy.WEEKS_AGO.length() - 1);
            return base.minusWeeks(Integer.parseInt(text));
        }
        if (text.endsWith(Lazy.MONTH_AGO)) {
            text = text.substring(0, text.length() - Lazy.MONTH_AGO.length() - 1);
            return base.minusMonths(Integer.parseInt(text));
        }
        if (text.endsWith(Lazy.MONTHS_AGO)) {
            text = text.substring(0, text.length() - Lazy.MONTHS_AGO.length() - 1);
            return base.minusMonths(Integer.parseInt(text));
        }
        if (text.endsWith(Lazy.YEAR_AGO)) {
            text = text.substring(0, text.length() - Lazy.YEAR_AGO.length() - 1);
            return base.minusYears(Integer.parseInt(text));
        }
        if (text.endsWith(Lazy.YEARS_AGO)) {
            text = text.substring(0, text.length() - Lazy.YEARS_AGO.length() - 1);
            return base.minusYears(Integer.parseInt(text));
        }
        throw new IllegalArgumentException("Unknown text: " + text);
    }

    private static class Lazy {

        private static final String MINUTES_AGO = "minutes ago";
        private static final String HOURS_AGO = "hours ago";
        private static final String DAY_AGO = "day ago";
        private static final String DAYS_AGO = "days ago";
        private static final String WEEK_AGO = "week ago";
        private static final String WEEKS_AGO = "weeks ago";
        private static final String MONTH_AGO = "month ago";
        private static final String MONTHS_AGO = "months ago";
        private static final String YEAR_AGO = "year ago";
        private static final String YEARS_AGO = "years ago";

        private static final Pattern DURATION_REGEX = Pattern
            .compile("((?<h>\\d{1,2}):)?(?<m>\\d{1,2}):(?<s>\\d{1,2})(\\.?(?<mi>\\d{1,3}))?");
    }

}
