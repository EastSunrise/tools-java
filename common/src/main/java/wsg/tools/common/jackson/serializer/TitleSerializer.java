package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * Serialize a object implementing {@link TitleSupplier} to a title.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class TitleSerializer<T extends TitleSupplier> extends AbstractNonNullSerializer<T> {

    protected TitleSerializer(Class<T> clazz) {
        super(clazz);
    }

    public static <T extends TitleSupplier> TitleSerializer<T> getInstance(Class<T> type) {
        return new TitleSerializer<>(type);
    }

    @Override
    protected void serializeNonNull(T value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        gen.writeString(value.getTitle());
    }
}
