package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import wsg.tools.common.function.TitleSupplier;

import java.io.IOException;

/**
 * Serializers for {@link Enum}.
 *
 * @author Kingen
 * @since 2020/7/19
 */
public final class EnumSerializers {

    public static <E extends Enum<E> & TitleSupplier> EnumTitleSerializer<E> getTitleSerializer(Class<E> eClass) {
        return new EnumTitleSerializer<>(eClass);
    }

    /**
     * Serialize an enum implementing {@link TitleSupplier}.
     */
    static class EnumTitleSerializer<E extends Enum<E> & TitleSupplier> extends TitleSerializer<E> {
        protected EnumTitleSerializer(Class<E> javaType) {
            super(javaType);
        }

        @Override
        protected void serializeNonNull(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("name", value.name().toLowerCase());
            gen.writeStringField("title", value.getTitle());
            gen.writeEndObject();
        }
    }
}
