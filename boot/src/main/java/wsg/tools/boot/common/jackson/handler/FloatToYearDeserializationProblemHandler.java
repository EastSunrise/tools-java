package wsg.tools.boot.common.jackson.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import java.io.IOException;
import java.time.Year;
import org.apache.commons.lang3.ClassUtils;

/**
 * Deserialize a float number to a {@link Year}, especially read from an excel file.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public class FloatToYearDeserializationProblemHandler extends DeserializationProblemHandler {

    @Override
    public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType,
        JsonToken t, JsonParser p, String failureMsg) throws IOException {
        if (JsonToken.VALUE_NUMBER_FLOAT == t && ClassUtils
            .isAssignable(targetType.getRawClass(), Year.class)) {
            float floatValue = p.getFloatValue();
            return Year.of((int) floatValue);
        }
        return super.handleUnexpectedToken(ctxt, targetType, t, p, failureMsg);
    }
}
