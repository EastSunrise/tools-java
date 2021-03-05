package wsg.tools.common.lang;

import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * Utility for {@link Enum}
 *
 * @author Kingen
 * @since 2020/6/17
 */
@UtilityClass
public class EnumUtilExt {

    /**
     * Get Enum from the name
     */
    public <A, E extends Enum<E> & AkaPredicate<A>> E deserializeAka(A object, Class<E> clazz) {
        return findOne(clazz, anEnum -> anEnum.alsoKnownAs(object), "Unknown aka '%s' for '%s'",
            object, clazz);
    }

    /**
     * Get Enum from the code
     */
    public <C, E extends Enum<E> & CodeSupplier<C>> E deserializeCode(C code, Class<E> clazz) {
        return findOne(clazz, anEnum -> anEnum.getCode().equals(code), "Unknown code '%d' for '%s'",
            code, clazz);
    }

    /**
     * Get Enum from the text
     */
    public <E extends Enum<E> & TextSupplier> E deserializeText(String text, Class<E> clazz,
        boolean ignoreCase) {
        if (ignoreCase) {
            return findOne(clazz, anEnum -> anEnum.getText().equalsIgnoreCase(text),
                "Unknown text '%s' for '%s'", text,
                clazz);
        }
        return findOne(clazz, anEnum -> anEnum.getText().equals(text), "Unknown text '%s' for '%s'",
            text, clazz);
    }

    /**
     * Get Enum from the title
     */
    public <E extends Enum<E> & TitleSupplier> E deserializeTitle(String title, Class<E> clazz,
        boolean ignoreCase) {
        if (ignoreCase) {
            return findOne(clazz, anEnum -> anEnum.getTitle().equalsIgnoreCase(title),
                "Unknown title '%s' for '%s'",
                title, clazz);
        }
        return findOne(clazz, anEnum -> anEnum.getTitle().equals(title),
            "Unknown title '%s' for '%s'", title, clazz);
    }

    /**
     * Get Enum matching the given predicate.
     */
    public <E extends Enum<E>> E deserialize(Class<E> clazz, Predicate<? super E> predicate) {
        return findOne(clazz, predicate, "Unknown enum '%s' by '%s'", clazz, predicate.toString());
    }

    private <T extends Enum<T>> T findOne(Class<T> clazz, Predicate<? super T> predicate,
        String message,
        Object... args) {
        T[] enums = clazz.getEnumConstants();
        for (T t : enums) {
            if (predicate.test(t)) {
                return t;
            }
        }
        throw new IllegalArgumentException(String.format(message, args));
    }
}
