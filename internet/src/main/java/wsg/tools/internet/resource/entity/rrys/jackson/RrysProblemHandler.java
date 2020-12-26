package wsg.tools.internet.resource.entity.rrys.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.internet.resource.entity.rrys.common.FormatEnum;
import wsg.tools.internet.resource.site.RrysSite;

import java.io.IOException;
import java.time.Year;

/**
 * Handle problems that occur when parsing {@link RrysSite} beans.
 *
 * @author Kingen
 * @since 2020/12/24
 */
public class RrysProblemHandler extends DeserializationProblemHandler {

    @Override
    public Object handleWeirdKey(DeserializationContext ctxt, Class<?> rawKeyType, String keyValue, String failureMsg) throws IOException {
        if (FormatEnum.class.equals(rawKeyType)) {
            return FormatEnum.of(keyValue);
        }
        return super.handleWeirdKey(ctxt, rawKeyType, keyValue, failureMsg);
    }

    @Override
    public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
        if (Year.class.equals(targetType) && StringUtils.isBlank(valueToConvert)) {
            return null;
        }
        if (FormatEnum.class.equals(targetType)) {
            return FormatEnum.of(valueToConvert);
        }
        return super.handleWeirdStringValue(ctxt, targetType, valueToConvert, failureMsg);
    }

    @Override
    public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType, JsonToken t, JsonParser p, String failureMsg) throws IOException {
        if ("category".equals(p.currentName()) && p.hasToken(JsonToken.VALUE_FALSE)) {
            return null;
        }
        return super.handleUnexpectedToken(ctxt, targetType, t, p, failureMsg);
    }
}
