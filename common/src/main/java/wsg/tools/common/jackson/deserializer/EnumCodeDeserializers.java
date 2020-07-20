package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.common.util.EnumUtilExt;

/**
 * Deserialize from code to an enum implementing {@link CodeSerializable}.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class EnumCodeDeserializers {

    public static <C, E extends Enum<E> & CodeSerializable<C>> EnumCodeDeserializer<C, E> getDeserializer(Class<C> cClass, Class<E> tClass) {
        return new EnumCodeDeserializer<>(tClass, cClass);
    }

    public static class EnumCodeDeserializer<Code, E extends Enum<E> & CodeSerializable<Code>> extends AbstractNonNullDeserializer<E, Code> {

        protected EnumCodeDeserializer(Class<E> javaType, Class<Code> jsonType) {
            super(javaType, jsonType);
        }

        @Override
        public E apply(Code code) {
            return EnumUtilExt.deserializeCode(code, javaType);
        }
    }
}