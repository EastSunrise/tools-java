package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import wsg.tools.common.util.function.TitleSupplier;

import java.io.IOException;

/**
 * Serialize a object implementing {@link TitleSupplier} to a title.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class TitleSerializer<JavaType extends TitleSupplier> extends AbstractNonNullSerializer<JavaType, String> {

    protected TitleSerializer(Class<JavaType> javaType) {
        super(javaType, String.class);
    }

    public static <T extends TitleSupplier> TitleSerializer<T> getInstance(Class<T> type) {
        return new TitleSerializer<>(type);
    }

    @Override
    protected void serializeNonNull(JavaType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getTitle());
    }
}
