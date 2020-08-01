package wsg.tools.boot.config.hanler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.apache.commons.lang3.ClassUtils;

import java.io.IOException;
import java.time.Year;

/**
 * Handlers for problems that occur when reading from excel-like data.
 *
 * @author Kingen
 * @since 2020/7/24
 */
public final class ExcelDeserializationProblemHandlers {

    public static class FloatToYearDeserializationProblemHandler extends DeserializationProblemHandler {

        public static final FloatToYearDeserializationProblemHandler INSTANCE = new FloatToYearDeserializationProblemHandler();

        protected FloatToYearDeserializationProblemHandler() {
        }

        @Override
        public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType, JsonToken t, JsonParser p, String failureMsg) throws IOException {
            if (t == JsonToken.VALUE_NUMBER_FLOAT && ClassUtils.isAssignable(targetType.getRawClass(), Year.class)) {
                float floatValue = p.getFloatValue();
                return Year.of((int) floatValue);
            }
            return super.handleUnexpectedToken(ctxt, targetType, t, p, failureMsg);
        }
    }
}
