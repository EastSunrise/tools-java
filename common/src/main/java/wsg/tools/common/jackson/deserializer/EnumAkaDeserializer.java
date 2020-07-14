package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.jackson.intf.AkaSerializable;
import wsg.tools.common.util.EnumUtilExt;

/**
 * Deserialize from aka to an enum implementing {@link AkaSerializable<Aka>}.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class EnumAkaDeserializer<Aka, E extends Enum<E> & AkaSerializable<Aka>> extends AbstractNonNullDeserializer<E, Aka> {

    protected EnumAkaDeserializer(Class<E> javaType, Class<Aka> jsonType) {
        super(javaType, jsonType);
    }

    public static <Aka, E extends Enum<E> & AkaSerializable<Aka>> EnumAkaDeserializer<Aka, E> getInstance(Class<Aka> akaClass, Class<E> eClass) {
        return new EnumAkaDeserializer<>(eClass, akaClass);
    }

    @Override
    public E apply(Aka aka) {
        return EnumUtilExt.deserializeAka(aka, javaType);
    }
}
