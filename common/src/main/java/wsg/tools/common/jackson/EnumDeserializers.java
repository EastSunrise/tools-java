package wsg.tools.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.AliasSupplier;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Provides common deserializers to deserialize enums.
 *
 * @author Kingen
 * @since 2021/5/24
 */
public final class EnumDeserializers {

    private EnumDeserializers() {
    }

    public static <E extends Enum<E> & CodeSupplier>
    JsonDeserializer<E> ofCode(Class<E> enumClass) {
        return new StdDeserializer<>(enumClass) {
            @Override
            @SuppressWarnings("unchecked")
            public E deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
                if (p.hasToken(JsonToken.VALUE_NULL)) {
                    return null;
                }
                if (p.hasToken(JsonToken.VALUE_STRING)) {
                    String text = p.getText();
                    if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                        && StringUtils.isEmpty(text)) {
                        return null;
                    }
                    return EnumUtilExt.valueOfCode(enumClass, text);
                }
                return (E) ctxt.handleUnexpectedToken(String.class, p.currentToken(), p,
                    "Unexpected token as a code to be deserialized to an enum.");
            }
        };
    }

    public static <E extends Enum<E> & IntCodeSupplier>
    JsonDeserializer<E> ofIntCode(Class<E> enumClass) {
        return new StdDeserializer<>(enumClass) {
            @Override
            @SuppressWarnings("unchecked")
            public E deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
                if (p.hasToken(JsonToken.VALUE_NULL)) {
                    return null;
                }
                if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                    return EnumUtilExt.valueOfIntCode(enumClass, p.getIntValue());
                }
                return (E) ctxt.handleUnexpectedToken(int.class, p.currentToken(), p,
                    "Unexpected token as an integer code to be deserialized to an enum.");
            }
        };
    }

    public static <E extends Enum<E> & AliasSupplier>
    JsonDeserializer<E> ofAlias(Class<E> enumClass) {
        return new StdDeserializer<>(enumClass) {
            @Override
            @SuppressWarnings("unchecked")
            public E deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
                if (p.hasToken(JsonToken.VALUE_NULL)) {
                    return null;
                }
                if (p.hasToken(JsonToken.VALUE_STRING)) {
                    String text = p.getText();
                    if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                        && StringUtils.isEmpty(text)) {
                        return null;
                    }
                    return EnumUtilExt.valueOfAlias(enumClass, text);
                }
                return (E) ctxt.handleUnexpectedToken(String.class, p.currentToken(), p,
                    "Unexpected token as alias to be deserialized to an enum.");
            }
        };
    }
}
