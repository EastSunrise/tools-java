package wsg.tools.common.lang;

import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;

import java.util.function.Predicate;

/**
 * Utility for {@link Enum}
 *
 * @author Kingen
 * @since 2020/6/17
 */
public class EnumUtilExt {

    /**
     * Get Enum from the name
     */
    public static <A, E extends Enum<E> & AkaPredicate<A>> E deserializeAka(A object, Class<E> clazz) {
        return findOne(clazz, anEnum -> anEnum.alsoKnownAs(object), "Unknown aka '%s' for '%s'", object, clazz.getName());
    }

    /**
     * Get Enum from the code
     */
    public static <C, E extends Enum<E> & CodeSupplier<C>> E deserializeCode(C code, Class<E> clazz) {
        return findOne(clazz, anEnum -> anEnum.getCode().equals(code), "Unknown code '%d' for '%s'", code, clazz.getName());
    }

    /**
     * Get Enum from the text
     */
    public static <E extends Enum<E> & TextSupplier> E deserializeText(String text, Class<E> clazz, boolean ignoreCase) {
        if (ignoreCase) {
            return findOne(clazz, anEnum -> anEnum.getText().equalsIgnoreCase(text), "Unknown text '%s' for '%s'", text, clazz.getName());
        }
        return findOne(clazz, anEnum -> anEnum.getText().equals(text), "Unknown text '%s' for '%s'", text, clazz.getName());
    }

    /**
     * Get Enum from the title
     */
    public static <E extends Enum<E> & TitleSupplier> E deserializeTitle(String title, Class<E> clazz, boolean ignoreCase) {
        if (ignoreCase) {
            return findOne(clazz, anEnum -> anEnum.getTitle().equalsIgnoreCase(title), "Unknown title '%s' for '%s'", title, clazz.getName());
        }
        return findOne(clazz, anEnum -> anEnum.getTitle().equals(title), "Unknown title '%s' for '%s'", title, clazz.getName());
    }

    /**
     * Get Enum matching the given predicate.
     */
    public static <E extends Enum<E>> E deserialize(Class<E> clazz, Predicate<? super E> predicate) {
        return findOne(clazz, predicate, "Unknown enum '%s' by '%s'", clazz.getName(), predicate.toString());
    }

    private static <T extends Enum<T>> T findOne(Class<T> clazz, Predicate<? super T> predicate, String message, Object... args) {
        T[] enums = clazz.getEnumConstants();
        for (T t : enums) {
            if (predicate.test(t)) {
                return t;
            }
        }
        throw new IllegalArgumentException(String.format(message, args));
    }
}
