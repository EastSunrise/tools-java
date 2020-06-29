package wsg.tools.common.util;

import wsg.tools.common.jackson.intf.AkaSerializable;
import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.common.jackson.intf.TextSerializable;
import wsg.tools.common.jackson.intf.TitleSerializable;

import java.util.Arrays;

/**
 * Utility for {@link Enum}
 *
 * @author Kingen
 * @since 2020/6/17
 */
public class EnumUtils {

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
    public static <A, T extends Enum<T> & AkaSerializable<A>> T deserializeAka(A object, Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return AssertUtils.findOne(Arrays.stream(enums), anEnum -> anEnum.alsoKnownAs(object),
                "Unknown object %s for %s", object, clazz.getName());
    }

    /**
     * Get Enum from the code
     */
    public static <C, T extends Enum<T> & CodeSerializable<C>> T deserializeCode(C code, Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return AssertUtils.findOne(Arrays.stream(enums), anEnum -> anEnum.getCode().equals(code),
                "Unknown code %d for %s", code, clazz.getName());
    }

    /**
     * Get Enum from the text
     */
    public static <T extends Enum<T> & TextSerializable> T deserializeText(String text, Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return AssertUtils.findOne(Arrays.stream(enums), anEnum -> anEnum.getText().equals(text),
                "Unknown text %s for %s", text, clazz.getName());
    }

    /**
     * Get Enum from the title
     */
    public static <T extends Enum<T> & TitleSerializable> T deserializeTitle(String title, Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return AssertUtils.findOne(Arrays.stream(enums), anEnum -> anEnum.getTitle().equals(title),
                "Unknown title %s for %s", title, clazz.getName());
    }
}
