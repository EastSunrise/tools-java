package wsg.tools.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

/**
 * Deserializes a value of type {@code T} to an enum with {@link #valueOf(Class, Object)}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public abstract class AbstractEnumDeserializer<T, E extends Enum<E>> extends StdDeserializer<E> {

    private final Class<T> valueType;

    protected AbstractEnumDeserializer(Class<T> valueType, Class<E> eClass) {
        super(eClass);
        this.valueType = valueType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return null;
        }
        T t = p.readValueAs(valueType);
        if (t instanceof String && StringUtils.isEmpty((CharSequence) t)
            && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            return null;
        }
        return valueOf((Class<E>) handledType(), t);
    }

    /**
     * Converts the value to an enum.
     *
     * @param eClass type of the target enum
     * @param value  value to deserialize
     * @return the enum
     * @throws IllegalArgumentException if can't deserialize
     */
    public abstract E valueOf(Class<E> eClass, T value);
}
