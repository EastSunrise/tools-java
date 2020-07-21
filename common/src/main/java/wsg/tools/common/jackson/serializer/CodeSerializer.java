package wsg.tools.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import wsg.tools.common.jackson.intf.CodeSupplier;

import java.io.IOException;

/**
 * Serialize a object implementing {@link CodeSupplier <Code>} to a code.
 *
 * @author Kingen
 * @since 2020/6/27
 */
public class CodeSerializer<Code, JavaType extends CodeSupplier<Code>> extends AbstractNonNullSerializer<JavaType, Code> {

    protected CodeSerializer(Class<JavaType> javaType, Class<Code> jsonType) {
        super(javaType, jsonType);
    }

    public static <C, J extends CodeSupplier<C>> CodeSerializer<C, J> getInstance(Class<C> cClass, Class<J> jClass) {
        return new CodeSerializer<>(jClass, cClass);
    }

    @Override
    protected void serializeNonNull(JavaType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.getCode());
    }
}
