package wsg.tools.internet.video.jackson.handler;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.util.AssertUtils;

import java.io.IOException;
import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle irregular string of {@link Year}.
 *
 * @author Kingen
 * @since 2020/7/18
 */
public class YearDeserializationProblemHandler extends DeserializationProblemHandler {

    public static final YearDeserializationProblemHandler INSTANCE = new YearDeserializationProblemHandler();
    private static final Pattern YEAR_REGEX = Pattern.compile("(\\d{4})(–(\\d{4})?)?");

    protected YearDeserializationProblemHandler() {
    }

    @Override
    public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
        if (StringUtils.isBlank(valueToConvert)) {
            return null;
        }
        if (Year.class.equals(targetType)) {
            Matcher matcher = AssertUtils.matches(YEAR_REGEX, valueToConvert);
            return Year.of(Integer.parseInt(matcher.group(1)));
        }
        return super.handleWeirdStringValue(ctxt, targetType, valueToConvert, failureMsg);
    }
}
