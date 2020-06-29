package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.deserializer.base.AbstractStringDeserializer;
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
public class DurationExtDeserializer extends AbstractStringDeserializer<Duration> {

    private static final Pattern DURATION_REGEX = Pattern.compile("(\\w+: )?((\\d+)\\s?h\\s)?(\\d+(,\\d{3})*)(-(\\d+))?\\s?(min|分钟)?(\\s?\\([^()]*\\))*");

    private static final String DURATION_START = "P";

    @Override
    public Duration toNonNullT(String text) {
        if (text.startsWith(DURATION_START)) {
            return Duration.parse(text.replace(",", ""));
        }
        Matcher matcher = AssertUtils.matches(DURATION_REGEX, text);
        Duration duration = Duration.ZERO;
        if (matcher.group(3) != null) {
            duration = duration.plusHours(Integer.parseInt(matcher.group(3)));
        }
        String min = matcher.group(4);
        if (matcher.group(5) != null) {
            min = min.replace(Constants.NUMBER_DELIMITER, "");
        }
        int minutes = Integer.parseInt(min);
        if (matcher.group(7) != null) {
            minutes = (minutes + Integer.parseInt(matcher.group(7))) / 2;
        }
        return duration.plusMinutes(minutes);
    }
}
