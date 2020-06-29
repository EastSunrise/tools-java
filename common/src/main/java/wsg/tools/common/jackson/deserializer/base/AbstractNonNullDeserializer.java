package wsg.tools.common.jackson.deserializer.base;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Deserialize a not-null serialized object to a non-null instance of java object.
 *
 * @author Kingen
 * @since 2020/6/27
 */
public abstract class AbstractNonNullDeserializer<S, T> extends JsonDeserializer<T> {

    private Class<S> sClass;

    public AbstractNonNullDeserializer(Class<S> sClass) {
        this.sClass = sClass;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext context) throws IOException {
        S s = p.readValueAs(sClass);
        if (s == null) {
            return null;
        }
        return toNonNullT(s);
    }

    /**
     * Obtains a non-null instance of object from a text.
     *
     * @param s the serialized object to deserialize, not null.
     * @return the object
     * @throws JsonParseException can't parse
     */
    public abstract T toNonNullT(S s) throws JsonParseException;
}
