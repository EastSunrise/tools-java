package wsg.tools.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;

import java.io.IOException;

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

    public static class EnumCodeDeserializer<Code, E extends Enum<E> & CodeSupplier<Code>> extends StdDeserializer<E> {

        private final Class<Code> codeClass;

        protected EnumCodeDeserializer(Class<E> eClass, Class<Code> codeClass) {
            super(eClass);
            this.codeClass = codeClass;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (parser.hasToken(JsonToken.VALUE_NULL)) {
                return null;
            }
            Code code = parser.readValueAs(codeClass);
            return EnumUtilExt.deserializeCode(code, (Class<E>) handledType());
        }
    }

    public static class EnumAkaDeserializer<Aka, E extends Enum<E> & AkaPredicate<Aka>> extends StdDeserializer<E> {

        private final Class<Aka> akaClass;

        public EnumAkaDeserializer(Class<E> eClass, Class<Aka> akaClass) {
            super(eClass);
            this.akaClass = akaClass;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (parser.hasToken(JsonToken.VALUE_NULL)) {
                return null;
            }
            Aka aka = parser.readValueAs(akaClass);
            return EnumUtilExt.deserializeAka(aka, (Class<E>) handledType());
        }
    }

    public static class EnumTextDeserializer<E extends Enum<E> & TextSupplier> extends AbstractStringDeserializer<E> {

        protected EnumTextDeserializer(Class<E> eClass) {
            super(eClass);
        }

        @Override
        protected E parseText(String text) {
            return EnumUtilExt.deserializeText(text, clazz);
        }
    }

    public static class EnumTitleDeserializer<E extends Enum<E> & TitleSupplier> extends AbstractStringDeserializer<E> {

        protected EnumTitleDeserializer(Class<E> eClass) {
            super(eClass);
        }

        @Override
        protected E parseText(String title) {
            return EnumUtilExt.deserializeTitle(title, clazz);
        }
    }
}
