package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Base serializer to pre-handle null objects.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public abstract class AbstractNonNullSerializer<JavaType, JsonType> extends JsonSerializer<JavaType> {

    protected Class<JavaType> javaType;
    protected Class<JsonType> jsonType;

    protected AbstractNonNullSerializer(Class<JavaType> javaType, Class<JsonType> jsonType) {
        this.javaType = javaType;
        this.jsonType = jsonType;
    }

    @Override
    public void serialize(JavaType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
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

    @Override
    public Class<JavaType> handledType() {
        return javaType;
    }
}
