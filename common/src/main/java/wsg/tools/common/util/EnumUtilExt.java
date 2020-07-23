package wsg.tools.common.util;

import wsg.tools.common.function.AkaPredicate;
import wsg.tools.common.function.CodeSupplier;
import wsg.tools.common.function.TextSupplier;
import wsg.tools.common.function.TitleSupplier;

import java.util.Arrays;

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
    public static <T extends Enum<T>> T deserialize(String name, Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return AssertUtils.findOne(Arrays.stream(enums), anEnum -> anEnum.name().equals(name),
                "Unknown name %s for %s", name, clazz.getName());
    }

    /**
     * Get Enum from the name
     */
    public static <T extends Enum<T>> T deserializeIgnoreCase(String name, Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return AssertUtils.findOne(Arrays.stream(enums), anEnum -> anEnum.name().equalsIgnoreCase(name),
                "Unknown name %s for %s", name, clazz.getName());
    }

    /**
     * Get Enum from the name
     */
    public static <A, T extends Enum<T> & AkaPredicate<A>> T deserializeAka(A object, Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return AssertUtils.findOne(Arrays.stream(enums), anEnum -> anEnum.alsoKnownAs(object),
                "Unknown object %s for %s", object, clazz.getName());
    }

    /**
     * Get Enum from the code
     */
    public static <C, T extends Enum<T> & CodeSupplier<C>> T deserializeCode(C code, Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return AssertUtils.findOne(Arrays.stream(enums), anEnum -> anEnum.getCode().equals(code),
                "Unknown code %d for %s", code, clazz.getName());
    }

    /**
     * Get Enum from the text
     */
    public static <T extends Enum<T> & TextSupplier> T deserializeText(String text, Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return AssertUtils.findOne(Arrays.stream(enums), anEnum -> anEnum.getText().equals(text),
                "Unknown text %s for %s", text, clazz.getName());
    }

    /**
     * Get Enum from the title
     */
    public static <T extends Enum<T> & TitleSupplier> T deserializeTitle(String title, Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return AssertUtils.findOne(Arrays.stream(enums), anEnum -> anEnum.getTitle().equals(title),
                "Unknown title %s for %s", title, clazz.getName());
    }
}
