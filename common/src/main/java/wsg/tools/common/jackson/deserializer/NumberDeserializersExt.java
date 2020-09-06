package wsg.tools.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import wsg.tools.common.constant.SignConstants;

import java.io.IOException;

/**
 * Extended deserializers for {@link Number}/
 *
 * @author Kingen
 * @since 2020/6/28
 */
public final class NumberDeserializersExt {

    public static class LongDeserializer extends BaseNumberDeserializer<Long> {

        public static final LongDeserializer INSTANCE = new LongDeserializer();

        protected LongDeserializer() {
            super(Long.class);
        }

        @Override
        public Long parseNumber(String text) {
            return Long.parseLong(text);
        }
    }

    public static abstract class BaseNumberDeserializer<T extends Number> extends StdDeserializer<T> {

        protected BaseNumberDeserializer(Class<T> javaType) {
            super(javaType);
        }

        @Override
        @SuppressWarnings("unchecked")
        public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (parser.hasToken(JsonToken.VALUE_STRING)) {
                return parseNumber(parser.getText().replace(SignConstants.COMMA, ""));
            }
            return parser.readValueAs((Class<T>) handledType());
        }

        /**
         * Obtains a number from a string
         *
         * @param text the string to parse
         * @return a number
         */
        public abstract T parseNumber(String text);
    }
}
