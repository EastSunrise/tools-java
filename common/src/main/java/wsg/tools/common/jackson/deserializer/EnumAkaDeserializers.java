package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.jackson.intf.AkaPredicate;
import wsg.tools.common.util.EnumUtilExt;

/**
 * Deserialize from aka to an enum implementing {@link AkaPredicate}.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class EnumAkaDeserializers {

    public static <Aka, E extends Enum<E> & AkaPredicate<Aka>> EnumAkaDeserializer<Aka, E> getDeserializer(Class<Aka> akaClass, Class<E> eClass) {
        return new EnumAkaDeserializer<>(eClass, akaClass);
    }

    public static <E extends Enum<E> & AkaPredicate<String>> EnumAkaDeserializer<String, E> getStringDeserializer(Class<E> eClass) {
        return getDeserializer(String.class, eClass);
    }

    protected static class EnumAkaDeserializer<Aka, E extends Enum<E> & AkaPredicate<Aka>> extends AbstractNonNullDeserializer<E, Aka> {

        protected EnumAkaDeserializer(Class<E> javaType, Class<Aka> jsonType) {
            super(javaType, jsonType);
        }

        @Override
        public E apply(Aka aka) {
            return EnumUtilExt.deserializeAka(aka, javaType);
        }
    }
}
