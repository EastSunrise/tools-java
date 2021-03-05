package wsg.tools.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.function.BiPredicate;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * Deserializers for {@link Enum} which usually implements {@link CodeSupplier}
 *
 * @author Kingen
 * @since 2020/7/23
 */
@UtilityClass
public class EnumDeserializers {

    public <E extends Enum<E>, U> EnumDeserializer<E, U> getDeserializer(Class<E> eClass,
        Class<U> uClass,
        BiPredicate<E, U> predicate) {
        return new EnumDeserializer<>(eClass, uClass, predicate);
    }

    public <C, E extends Enum<E> & CodeSupplier<C>> EnumCodeDeserializer<C, E> getCodeDeserializer(
        Class<C> cClass,
        Class<E> eClass) {
        return new EnumCodeDeserializer<>(eClass, cClass);
    }

    public <Aka, E extends Enum<E> & AkaPredicate<Aka>> EnumAkaDeserializer<Aka, E>
    getAkaDeserializer(Class<Aka> akaClass, Class<E> eClass) {
        return new EnumAkaDeserializer<>(eClass, akaClass);
    }

    public <E extends Enum<E> & TextSupplier> EnumTextDeserializer<E> getTextDeserializer(
        Class<E> tClass) {
        return new EnumTextDeserializer<>(tClass);
    }

    public <E extends Enum<E> & TitleSupplier> EnumTitleDeserializer<E> getTitleDeserializer(
        Class<E> tClass) {
        return new EnumTitleDeserializer<>(tClass);
    }

    public class EnumDeserializer<E extends Enum<E>, U> extends StdDeserializer<E> {

        private final Class<U> uClass;

        private final BiPredicate<E, U> predicate;

        protected EnumDeserializer(Class<E> eClass, Class<U> uClass, BiPredicate<E, U> predicate) {
            super(eClass);
            this.uClass = uClass;
            this.predicate = predicate;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (parser.hasToken(JsonToken.VALUE_NULL)) {
                return null;
            }
            U u = parser.readValueAs(uClass);
            if (u instanceof String && StringUtils.isBlank((CharSequence) u)
                && context.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                return null;
            }
            try {
                return EnumUtilExt.deserialize((Class<E>) handledType(), e -> predicate.test(e, u));
            } catch (IllegalArgumentException e) {
                return (E) context
                    .handleWeirdNativeValue(context.constructType(handledType()), u, parser);
            }
        }
    }

    public class EnumCodeDeserializer<Code, E extends Enum<E> & CodeSupplier<Code>> extends
        StdDeserializer<E> {

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
            if (code instanceof String && StringUtils.isBlank((CharSequence) code)
                && context.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                return null;
            }
            try {
                return EnumUtilExt.deserializeCode(code, (Class<E>) handledType());
            } catch (IllegalArgumentException e) {
                return (E) context
                    .handleWeirdNativeValue(context.constructType(handledType()), code, parser);
            }
        }
    }

    public class EnumAkaDeserializer<Aka, E extends Enum<E> & AkaPredicate<Aka>> extends
        StdDeserializer<E> {

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
            if (aka instanceof String && StringUtils.isBlank((CharSequence) aka)
                && context.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                return null;
            }
            try {
                return EnumUtilExt.deserializeAka(aka, (Class<E>) handledType());
            } catch (IllegalArgumentException e) {
                return (E) context
                    .handleWeirdNativeValue(context.constructType(handledType()), aka, parser);
            }
        }
    }

    public class EnumTextDeserializer<E extends Enum<E> & TextSupplier> extends
        AbstractStringDeserializer<E> {

        protected EnumTextDeserializer(Class<E> eClass) {
            super(eClass);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected E parseText(String text, DeserializationContext context) throws IOException {
            try {
                return EnumUtilExt.deserializeText(text, getClazz(),
                    context.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
            } catch (IllegalArgumentException e) {
                return (E) context.handleWeirdStringValue(handledType(), text, e.getMessage());
            }
        }
    }

    public class EnumTitleDeserializer<E extends Enum<E> & TitleSupplier> extends
        AbstractStringDeserializer<E> {

        protected EnumTitleDeserializer(Class<E> eClass) {
            super(eClass);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected E parseText(String title, DeserializationContext context) throws IOException {
            try {
                return EnumUtilExt.deserializeTitle(title, getClazz(),
                    context.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
            } catch (IllegalArgumentException e) {
                return (E) context.handleWeirdStringValue(handledType(), title, e.getMessage());
            }
        }
    }
}
