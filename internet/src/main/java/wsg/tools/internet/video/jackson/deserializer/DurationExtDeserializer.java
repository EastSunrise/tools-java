package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.deserializer.AbstractNotBlankDeserializer;
import wsg.tools.common.util.AssertUtils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserialize a text to an instance of {@link java.time.Duration} as runtime.
 *
 * @author Kingen
 * @since 2020/6/20
 */
public class DurationExtDeserializer extends AbstractNotBlankDeserializer<Duration> {

    public static final DurationExtDeserializer INSTANCE = new DurationExtDeserializer();

    private static final Pattern DURATION_REGEX = Pattern.compile("(\\w+: )?((\\d+)\\s?h\\s)?(\\d+(,\\d{3})*)([-–](\\d+))?\\s?(min|分钟)?(\\s?\\([^()]*\\))*");
    private static final int HOUR_GROUP = 3;
    private static final int MINUTE_GROUP = 5;
    private static final int MINUTE_MAX_GROUP = 7;
    private static final String DURATION_START = "P";

    protected DurationExtDeserializer() {
        super(Duration.class);
    }

    @Override
    protected Duration parseText(String text) {
        if (text.startsWith(DURATION_START)) {
            return Duration.parse(text.replace(",", ""));
        }
        Matcher matcher = AssertUtils.matches(DURATION_REGEX, text);
        Duration duration = Duration.ZERO;
        if (matcher.group(HOUR_GROUP) != null) {
            duration = duration.plusHours(Integer.parseInt(matcher.group(HOUR_GROUP)));
        }
        String min = matcher.group(4);
        if (matcher.group(MINUTE_GROUP) != null) {
            min = min.replace(Constants.COMMA_DELIMITER, "");
        }
        int minutes = Integer.parseInt(min);
        if (matcher.group(MINUTE_MAX_GROUP) != null) {
            minutes = (minutes + Integer.parseInt(matcher.group(MINUTE_MAX_GROUP))) / 2;
        }
        return duration.plusMinutes(minutes);
    }
}
