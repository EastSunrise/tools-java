package wsg.tools.internet.video.jackson.handler;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.util.AssertUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle irregular string when deserializing {@link LocalDate} which is release date..
 *
 * @author Kingen
 * @since 2020/9/2
 */
public class ReleaseDeserializationProblemHandler extends DeserializationProblemHandler {

    public static final ReleaseDeserializationProblemHandler INSTANCE = new ReleaseDeserializationProblemHandler();
    private static final Pattern RELEASE_REGEX = Pattern.compile("((\\d{4})(-(\\d{2})(-(\\d{2}))?)?)(\\(([^()]+)\\))?");

    protected ReleaseDeserializationProblemHandler() {
    }

    @Override
    public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String text, String failureMsg) throws IOException {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        if (LocalDate.class.equals(targetType)) {
            Matcher matcher = AssertUtils.matches(RELEASE_REGEX, text);
            int month = matcher.group(3) == null ? 1 : Integer.parseInt(matcher.group(4));
            int day = matcher.group(5) == null ? 1 : Integer.parseInt(matcher.group(6));
            return LocalDate.of(Integer.parseInt(matcher.group(2)), month, day);
        }
        return super.handleWeirdStringValue(ctxt, targetType, text, failureMsg);
    }
}
