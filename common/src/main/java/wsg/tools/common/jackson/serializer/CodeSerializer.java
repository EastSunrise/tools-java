package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import wsg.tools.common.util.function.CodeSupplier;

/**
 * Serialize a object implementing {@link CodeSupplier} to a code.
 *
 * @author Kingen
 * @since 2020/6/27
 */
public class CodeSerializer<C, T extends CodeSupplier<C>> extends AbstractNonNullSerializer<T> {

    protected CodeSerializer(Class<T> clazz) {
        super(clazz);
    }

    public static <C, J extends CodeSupplier<C>> CodeSerializer<C, J> getInstance(Class<J> clazz) {
        return new CodeSerializer<>(clazz);
    }

    @Override
    protected void serializeNonNull(T value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        gen.writeObject(value.getCode());
    }
}
