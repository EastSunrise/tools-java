package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * Base serializer to pre-handle null objects.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public abstract class AbstractNonNullSerializer<T> extends StdSerializer<T> {

    protected AbstractNonNullSerializer(Class<T> clazz) {
        super(clazz);
    }

    /**
     * Alternate constructor that is (alas!) needed to work around kinks of generic type handling
     */
    protected AbstractNonNullSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            serializeNonNull(value, gen, serializers);
        }
    }

    /**
     * Serialize a non-null value
     *
     * @param value       value to serialize, not null
     * @param gen         json generator
     * @param serializers serializer provider
     * @throws IOException io exception
     */
    protected abstract void serializeNonNull(T value, JsonGenerator gen,
        SerializerProvider serializers)
        throws IOException;
}
