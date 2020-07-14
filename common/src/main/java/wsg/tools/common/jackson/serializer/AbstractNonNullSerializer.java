package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.function.Function;

/**
 * Base serializer to pre-handle null objects.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public abstract class AbstractNonNullSerializer<JavaType, JsonType> extends JsonSerializer<JavaType> implements Function<JavaType, JsonType> {

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
        gen.writeObject(apply(value));
    }

    @Override
    public Class<JavaType> handledType() {
        return javaType;
    }
}
