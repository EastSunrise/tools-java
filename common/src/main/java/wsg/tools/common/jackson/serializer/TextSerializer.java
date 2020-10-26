package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import wsg.tools.common.util.function.TextSupplier;

import java.io.IOException;

/**
 * Serialize a object implementing {@link TextSupplier} to a text.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class TextSerializer<JavaType extends TextSupplier> extends AbstractNonNullSerializer<JavaType, String> {

    protected TextSerializer(Class<JavaType> javaType) {
        super(javaType, String.class);
    }

    public static <T extends TextSupplier> TextSerializer<T> getInstance(Class<T> type) {
        return new TextSerializer<>(type);
    }

    @Override
    protected void serializeNonNull(JavaType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getText());
    }
}
