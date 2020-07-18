package wsg.tools.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.Getter;

import java.io.IOException;
import java.util.function.Function;

/**
 * Base deserializer to pre-handle null objects.
 *
 * @author Kingen
 * @since 2020/6/27
 */
public abstract class AbstractNonNullDeserializer<JavaType, JsonType> extends JsonDeserializer<JavaType> implements Function<JsonType, JavaType> {

    @Getter
    protected Class<JavaType> javaType;
    @Getter
    protected Class<JsonType> jsonType;

    protected AbstractNonNullDeserializer(Class<JavaType> javaType, Class<JsonType> jsonType) {
        this.javaType = javaType;
        this.jsonType = jsonType;
    }

    @Override
    public JavaType deserialize(JsonParser p, DeserializationContext context) throws IOException {
        JsonType jsonType = p.readValueAs(this.jsonType);
        if (jsonType == null) {
            return null;
        }
        return apply(jsonType);
    }

    @Override
    public Class<?> handledType() {
        return javaType;
    }
}
