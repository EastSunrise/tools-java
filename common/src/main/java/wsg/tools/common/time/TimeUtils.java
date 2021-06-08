package wsg.tools.common.time;

import java.math.BigDecimal;
import java.time.Duration;
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
        Duration duration = Duration.ofMinutes(Long.parseLong(matcher.group("m")));
        String hour = matcher.group("h");
        if (null != hour) {
            duration = duration.plusHours(Long.parseLong(hour));
        }
        BigDecimal seconds = new BigDecimal(matcher.group("s"));
        return duration.plusNanos(seconds.scaleByPowerOfTen(9).longValueExact());
    }

    private static class Lazy {

        private static final Pattern DURATION_REGEX = Pattern
            .compile("(?:(?<h>\\d{1,2}):)?(?<m>\\d{1,2}):(?<s>\\d{1,2}(?:\\.\\d{1,9})?)");
    }

}
