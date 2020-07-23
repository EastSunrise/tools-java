package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.jackson.intf.AkaPredicate;
import wsg.tools.common.jackson.intf.CodeSupplier;
import wsg.tools.common.jackson.intf.TextSupplier;
import wsg.tools.common.jackson.intf.TitleSupplier;
import wsg.tools.common.util.EnumUtilExt;

/**
 * Deserializers for {@link Enum} which usually implements {@link CodeSupplier}
 *
 * @author Kingen
 * @since 2020/7/23
 */
public class EnumDeserializers {

    public static <C, E extends Enum<E> & CodeSupplier<C>> EnumCodeDeserializer<C, E> getCodeDeserializer(Class<C> cClass, Class<E> tClass) {
        return new EnumCodeDeserializer<>(tClass, cClass);
    }

    public static <Aka, E extends Enum<E> & AkaPredicate<Aka>> EnumAkaDeserializer<Aka, E> getAkaDeserializer(Class<Aka> akaClass, Class<E> eClass) {
        return new EnumAkaDeserializer<>(eClass, akaClass);
    }

    public static <E extends Enum<E> & TextSupplier> EnumTextDeserializer<E> getTextDeserializer(Class<E> tClass) {
        return new EnumTextDeserializer<>(tClass);
    }

    public static <E extends Enum<E> & TitleSupplier> EnumTitleDeserializer<E> getTitleDeserializer(Class<E> tClass) {
        return new EnumTitleDeserializer<>(tClass);
    }

    public static class EnumCodeDeserializer<Code, E extends Enum<E> & CodeSupplier<Code>> extends AbstractNonNullDeserializer<E, Code> {

        protected EnumCodeDeserializer(Class<E> javaType, Class<Code> jsonType) {
            super(javaType, jsonType);
        }

        @Override
        public E convert(Code code) {
            return EnumUtilExt.deserializeCode(code, getJavaType());
        }
    }

    public static class EnumAkaDeserializer<Aka, E extends Enum<E> & AkaPredicate<Aka>> extends AbstractNonNullDeserializer<E, Aka> {

        protected EnumAkaDeserializer(Class<E> javaType, Class<Aka> jsonType) {
            super(javaType, jsonType);
        }

        @Override
        public E convert(Aka aka) {
            return EnumUtilExt.deserializeAka(aka, getJavaType());
        }
    }

    public static class EnumTextDeserializer<E extends Enum<E> & TextSupplier> extends AbstractNotBlankDeserializer<E> {

        protected EnumTextDeserializer(Class<E> javaType) {
            super(javaType);
        }

        @Override
        protected E parseText(String text) {
            return EnumUtilExt.deserializeText(text, getJavaType());
        }
    }

    public static class EnumTitleDeserializer<E extends Enum<E> & TitleSupplier> extends AbstractNotBlankDeserializer<E> {

        protected EnumTitleDeserializer(Class<E> javaType) {
            super(javaType);
        }

        @Override
        protected E parseText(String text) {
            return EnumUtilExt.deserializeTitle(text, getJavaType());
        }
    }
}
