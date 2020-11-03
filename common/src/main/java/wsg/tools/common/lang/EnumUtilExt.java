package wsg.tools.common.lang;

import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;

import java.util.Objects;
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
    public static <A, T extends Enum<T> & AkaPredicate<A>> T deserializeAka(A object, Class<T> clazz) {
        return findOne(clazz, anEnum -> anEnum.alsoKnownAs(object),
                "Unknown aka %s for %s", object, clazz.getName());
    }

    /**
     * Get Enum from the code
     */
    public static <C, T extends Enum<T> & CodeSupplier<C>> T deserializeCode(C code, Class<T> clazz) {
        return findOne(clazz, anEnum -> anEnum.getCode().equals(code),
                "Unknown code %d for %s", code, clazz.getName());
    }

    /**
     * Get Enum from the text
     */
    public static <T extends Enum<T> & TextSupplier> T deserializeText(String text, Class<T> clazz) {
        return findOne(clazz, anEnum -> anEnum.getText().equals(text),
                "Unknown text %s for %s", text, clazz.getName());
    }

    /**
     * Get Enum from the title
     */
    public static <T extends Enum<T> & TitleSupplier> T deserializeTitle(String title, Class<T> clazz) {
        return findOne(clazz, anEnum -> Objects.equals(anEnum.getTitle(), title),
                "Unknown title %s for %s", title, clazz.getName());
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
