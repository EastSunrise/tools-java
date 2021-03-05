package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import wsg.tools.common.util.function.TextSupplier;

/**
 * Serialize a object implementing {@link TextSupplier} to a text.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class TextSerializer<T extends TextSupplier> extends AbstractNonNullSerializer<T> {

    protected TextSerializer(Class<T> clazz) {
        super(clazz);
    }

    public static <T extends TextSupplier> TextSerializer<T> getInstance(Class<T> type) {
        return new TextSerializer<>(type);
    }

    @Override
    protected void serializeNonNull(T value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        gen.writeString(value.getText());
    }
}
