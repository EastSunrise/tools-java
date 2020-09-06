package wsg.tools.internet.video.jackson.handler;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import wsg.tools.common.util.NumberUtilsExt;

import java.io.IOException;

/**
 * Deserialize numbers from string separated with comma every three digits.
 *
 * @author Kingen
 * @since 2020/9/5
 */
public class CommaSeparatedNumberDeserializationProblemHandler extends DeserializationProblemHandler {

    public static final CommaSeparatedNumberDeserializationProblemHandler INSTANCE = new CommaSeparatedNumberDeserializationProblemHandler();

    protected CommaSeparatedNumberDeserializationProblemHandler() {
    }

    @Override
    public Object handleWeirdStringValue(DeserializationContext context, Class<?> targetType, String text, String failureMsg) throws IOException {
        if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            return (int) NumberUtilsExt.parseCommaSeparatedNumber(text);
        }
        if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            return NumberUtilsExt.parseCommaSeparatedNumber(text);
        }
        return super.handleWeirdStringValue(context, targetType, text, failureMsg);
    }
}
