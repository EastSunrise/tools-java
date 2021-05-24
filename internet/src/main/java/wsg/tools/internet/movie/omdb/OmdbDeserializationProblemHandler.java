package wsg.tools.internet.movie.omdb;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import java.io.IOException;
import javax.annotation.Nonnull;
import wsg.tools.common.lang.NumberUtilsExt;

/**
 * @author Kingen
 * @since 2021/5/21
 */
class OmdbDeserializationProblemHandler extends DeserializationProblemHandler {

    private static final String NA = "N/A";

    OmdbDeserializationProblemHandler() {
    }

    @Override
    public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType,
        String valueToConvert, String failureMsg) throws IOException {
        if (NA.equals(valueToConvert)) {
            return null;
        }
        if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            return (int) NumberUtilsExt.parseCommaSeparatedNumber(valueToConvert);
        }
        if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            return NumberUtilsExt.parseCommaSeparatedNumber(valueToConvert);
        }
        return super.handleWeirdStringValue(ctxt, targetType, valueToConvert, failureMsg);
    }

    @Override
    public JavaType handleMissingTypeId(@Nonnull DeserializationContext ctxt, JavaType baseType,
        TypeIdResolver idResolver, String failureMsg) {
        return baseType;
    }
}
