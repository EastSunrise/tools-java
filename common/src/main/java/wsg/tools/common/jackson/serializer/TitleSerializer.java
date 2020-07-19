package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import wsg.tools.common.jackson.intf.TitleSerializable;

import java.io.IOException;

/**
 * Serialize a object implementing {@link TitleSerializable} to a title.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class TitleSerializer<JavaType extends TitleSerializable> extends AbstractNonNullSerializer<JavaType, String> {

    protected TitleSerializer(Class<JavaType> javaType) {
        super(javaType, String.class);
    }

    public static <T extends TitleSerializable> TitleSerializer<T> getInstance(Class<T> type) {
        return new TitleSerializer<>(type);
    }

    @Override
    protected void serializeNonNull(JavaType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getTitle());
    }
}
