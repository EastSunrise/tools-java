package wsg.tools.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

/**
 * Base deserializer to pre-handle blank string.
 *
 * @author Kingen
 * @since 2020/7/15
 */
public abstract class AbstractStringDeserializer<T> extends StdDeserializer<T> {

    private final Class<T> clazz;

    protected AbstractStringDeserializer(Class<T> clazz) {
        super(clazz);
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_NULL)) {
            return null;
        }
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            String text = parser.getText();
            if (context.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                && StringUtils.isBlank(text)) {
                return null;
            }
            return parseText(text, context);
        }
        return (T) context.handleUnexpectedToken(getClazz(), parser);
    }

    /**
     * Parses an object from a string.
     *
     * @param text    target text
     * @param context context that can be used to access information about this deserialization
     *                activity.
     * @return parsed object
     * @throws IOException i/o exception
     */
    protected abstract T parseText(String text, DeserializationContext context) throws IOException;

    Class<T> getClazz() {
        return clazz;
    }
}
