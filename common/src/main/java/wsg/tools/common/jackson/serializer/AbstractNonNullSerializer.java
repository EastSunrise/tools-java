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
public abstract class AbstractNonNullSerializer<JavaType, JsonType> extends StdSerializer<JavaType> {

    protected final Class<JsonType> jsonType;

    protected AbstractNonNullSerializer(Class<JavaType> javaType, Class<JsonType> jsonType) {
        super(javaType);
        this.jsonType = jsonType;
    }

    @Override
    public void serialize(JavaType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        serializeNonNull(value, gen, serializers);
    }

    /**
     * Serialize a non-null value
     *
     * @param value       value to serialize, not null
     * @param gen         json generator
     * @param serializers serializer provider
     * @throws IOException io exception
     */
    protected abstract void serializeNonNull(JavaType value, JsonGenerator gen, SerializerProvider serializers) throws IOException;
}
