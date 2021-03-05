package wsg.tools.internet.movie.common.jackson;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import java.io.IOException;
import wsg.tools.common.lang.NumberUtilsExt;

/**
 * Deserialize numbers from string separated with comma every three digits.
 *
 * @author Kingen
 * @since 2020/9/5
 */
public class CommaSeparatedNumberDeserializationProblemHandler extends DeserializationProblemHandler {

    @Override
    public Object handleWeirdStringValue(DeserializationContext context, Class<?> targetType,
        String text,
        String failureMsg) throws IOException {
        if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            return (int) NumberUtilsExt.parseCommaSeparatedNumber(text);
        }
        if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            return NumberUtilsExt.parseCommaSeparatedNumber(text);
        }
        return super.handleWeirdStringValue(context, targetType, text, failureMsg);
    }
}
