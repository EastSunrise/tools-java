package wsg.tools.common.jackson.serializer;

import wsg.tools.common.jackson.intf.CodeSerializable;

/**
 * Serialize a object implementing {@link CodeSerializable<Code>} to a code.
 *
 * @author Kingen
 * @since 2020/6/27
 */
public class CodeSerializer<Code, JavaType extends CodeSerializable<Code>> extends AbstractNonNullSerializer<JavaType, Code> {

    protected CodeSerializer(Class<JavaType> javaType, Class<Code> jsonType) {
        super(javaType, jsonType);
    }

    public static <C, J extends CodeSerializable<C>> CodeSerializer<C, J> getInstance(Class<C> cClass, Class<J> jClass) {
        return new CodeSerializer<>(jClass, cClass);
    }

    @Override
    public Code apply(JavaType javaType) {
        return javaType.getCode();
    }
}
