package wsg.tools.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

/**
 * Deserialize a value of type {@code T} to an enum with {@link #deserialize(Class, Object)}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public abstract class AbstractEnumDeserializer<T, E extends Enum<E>> extends StdDeserializer<E> {

    private final Class<T> tClass;

    AbstractEnumDeserializer(Class<T> tClass, Class<E> eClass) {
        super(eClass);
        this.tClass = tClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return null;
        }
        T t = p.readValueAs(tClass);
        if (t instanceof String && StringUtils.isBlank((CharSequence) t)
            && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            return null;
        }
        try {
            return deserialize((Class<E>) handledType(), t);
        } catch (IllegalArgumentException e) {
            return (E) ctxt
                .handleWeirdNativeValue(ctxt.constructType(handledType()), t, p);
        }
    }

    /**
     * Deserialize the value to an enum.
     *
     * @param eClass type of the target enum
     * @param value  value to deserialize
     * @return the enum
     * @throws IllegalArgumentException if can't deserialize
     */
    public abstract E deserialize(Class<E> eClass, T value);
}
