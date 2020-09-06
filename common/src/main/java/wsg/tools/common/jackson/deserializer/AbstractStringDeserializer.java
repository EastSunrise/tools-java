package wsg.tools.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;

/**
 * Base deserializer to pre-handle blank string.
 *
 * @author Kingen
 * @since 2020/7/15
 */
public abstract class AbstractStringDeserializer<T> extends StdDeserializer<T> {

    protected final Class<T> clazz;

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
            return parseText(parser.getText());
        }
        return (T) context.handleUnexpectedToken(clazz, parser);
    }

    /**
     * Obtains an object from a string.
     *
     * @param text string
     * @return parsed object
     * @throws InvalidFormatException invalid format
     */
    protected abstract T parseText(String text) throws InvalidFormatException;
}
