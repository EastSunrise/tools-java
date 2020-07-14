package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.common.util.EnumUtilExt;

/**
 * Deserialize from code to an enum implementing {@link CodeSerializable<Code>}.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class EnumCodeDeserializer<Code, E extends Enum<E> & CodeSerializable<Code>> extends AbstractNonNullDeserializer<E, Code> {

    protected EnumCodeDeserializer(Class<E> javaType, Class<Code> jsonType) {
        super(javaType, jsonType);
    }

    public static <C, T extends Enum<T> & CodeSerializable<C>> EnumCodeDeserializer<C, T> getInstance(Class<C> cClass, Class<T> tClass) {
        return new EnumCodeDeserializer<>(tClass, cClass);
    }

    @Override
    public E apply(Code code) {
        return EnumUtilExt.deserializeCode(code, javaType);
    }
}
