package wsg.tools.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Base deserializer to pre-handle null objects.
 *
 * @author Kingen
 * @since 2020/6/27
 */
public abstract class AbstractNonNullDeserializer<JavaType, JsonType> extends StdDeserializer<JavaType> {

    protected Class<JsonType> jsonType;

    protected AbstractNonNullDeserializer(Class<JavaType> javaType, Class<JsonType> jsonType) {
        super(javaType);
        this.jsonType = jsonType;
    }

    @Override
    public JavaType deserialize(JsonParser p, DeserializationContext context) throws IOException {
        JsonType jsonType = p.readValueAs(this.jsonType);
        if (jsonType == null) {
            return null;
        }
        return convert(jsonType);
    }

    /**
     * Convert a json object to a java object of given type.
     *
     * @param jsonType the json object
     * @return java object
     */
    public abstract JavaType convert(JsonType jsonType);

    @SuppressWarnings("unchecked")
    protected Class<JavaType> getJavaType() {
        return (Class<JavaType>) handledType();
    }
}
