package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import wsg.tools.common.jackson.intf.TextSerializable;

import java.io.IOException;

/**
 * Serialize a object implementing {@link TextSerializable} to a text.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class TextSerializer<JavaType extends TextSerializable> extends AbstractNonNullSerializer<JavaType, String> {

    protected TextSerializer(Class<JavaType> javaType) {
        super(javaType, String.class);
    }

    public static <T extends TextSerializable> TextSerializer<T> getInstance(Class<T> type) {
        return new TextSerializer<>(type);
    }

    @Override
    protected void serializeNonNull(JavaType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getText());
    }
}
