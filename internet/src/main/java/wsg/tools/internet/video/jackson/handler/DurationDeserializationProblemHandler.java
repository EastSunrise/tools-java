package wsg.tools.internet.video.jackson.handler;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.SignConstants;
import wsg.tools.common.util.AssertUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle irregular string when deserializing {@link Duration}.
 *
 * @author Kingen
 * @since 2020/9/1
 */
public class DurationDeserializationProblemHandler extends DeserializationProblemHandler {

    public static final DurationDeserializationProblemHandler INSTANCE = new DurationDeserializationProblemHandler();

    private static final Pattern DURATION_REGEX = Pattern.compile("([\\s\\w]+: )?((\\d+)\\s?h\\s)?(\\d+(,\\d{3})*)([-–](\\d+))?\\s?(min|分钟)?(\\s?\\([^()]*\\))*");
    private static final int HOUR_GROUP = 3;
    private static final int MINUTE_GROUP = 5;
    private static final int MINUTE_MAX_GROUP = 7;

    protected DurationDeserializationProblemHandler() {}

    @Override
    public Object handleWeirdStringValue(DeserializationContext context, Class<?> targetType, String text, String failureMsg) throws IOException {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        if (Duration.class.equals(targetType)) {
            Matcher matcher = AssertUtils.matches(DURATION_REGEX, text);
            Duration duration = Duration.ZERO;
            if (matcher.group(HOUR_GROUP) != null) {
                duration = duration.plusHours(Integer.parseInt(matcher.group(HOUR_GROUP)));
            }
            String min = matcher.group(4);
            if (matcher.group(MINUTE_GROUP) != null) {
                min = min.replace(SignConstants.COMMA, "");
            }
            int minutes = Integer.parseInt(min);
            if (matcher.group(MINUTE_MAX_GROUP) != null) {
                minutes = (minutes + Integer.parseInt(matcher.group(MINUTE_MAX_GROUP))) / 2;
            }
            return duration.plusMinutes(minutes);
        }
        return super.handleWeirdStringValue(context, targetType, text, failureMsg);
    }
}
