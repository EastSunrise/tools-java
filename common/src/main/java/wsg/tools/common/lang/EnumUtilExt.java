package wsg.tools.common.lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
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
public final class EnumUtilExt {

    private static final Map<Class<?>, Map<?, String>> CODES = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> NAMES = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> TEXTS = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> TEXTS_IGNORE_CASE = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> TITLES = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> TITLES_IGNORE_CASE = new HashMap<>(1);

    private EnumUtilExt() {
    }

    /**
     * Deserializes an enum from the given name, ignoring case.
     */
    public static <E extends Enum<E>> E deserializeIgnoreCase(@Nonnull String name,
        Class<E> clazz) {
        Map<String, String> map = NAMES.get(clazz);
        if (map == null) {
            map = Arrays.stream(clazz.getEnumConstants())
                .collect(Collectors.toMap(e -> e.name().toUpperCase(Locale.ROOT), Enum::name));
            NAMES.put(clazz, map);
        }
        String realName = map.get(name.toUpperCase(Locale.ROOT));
        if (realName != null) {
            return Enum.valueOf(clazz, realName);
        }
        throw new IllegalArgumentException(
            String.format("Unknown name '%s' for '%s'", name, clazz));
    }

    /**
     * Get Enum from the aka.
     */
    public static <A, E extends Enum<E> & AkaPredicate<A>> E deserializeAka(@Nonnull A other,
        Class<E> clazz) {
        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (e.alsoKnownAs(other)) {
                return e;
            }
        }
        throw new IllegalArgumentException(
            String.format("Unknown aka '%s' for '%s'", other, clazz));
    }

    /**
     * Get Enum from the code
     */
    public static <C, E extends Enum<E> & CodeSupplier<C>> E deserializeCode(@Nonnull C code,
        Class<E> clazz) {
        Map<?, String> map = CODES.get(clazz);
        if (map == null) {
            map = Arrays.stream(clazz.getEnumConstants())
                .collect(Collectors.toMap(CodeSupplier::getCode, Enum::name));
            CODES.put(clazz, map);
        }
        String name = map.get(code);
        if (name != null) {
            return Enum.valueOf(clazz, name);
        }
        throw new IllegalArgumentException(
            String.format("Unknown code '%s' for '%s'", code, clazz));
    }

    /**
     * Get Enum from the text
     */
    public static <E extends Enum<E> & TextSupplier> E deserializeText(@Nonnull String text,
        Class<E> clazz, boolean ignoreCase) {
        String name;
        if (ignoreCase) {
            Map<String, String> map = TEXTS_IGNORE_CASE.get(clazz);
            if (map == null) {
                map = Arrays.stream(clazz.getEnumConstants())
                    .collect(Collectors.toMap(
                        e -> e.getText().toUpperCase(Locale.ROOT), Enum::name
                    ));
                TEXTS_IGNORE_CASE.put(clazz, map);
            }
            name = map.get(text.toUpperCase(Locale.ROOT));
        } else {
            Map<String, String> map = TEXTS.get(clazz);
            if (map == null) {
                map = Arrays.stream(clazz.getEnumConstants())
                    .collect(Collectors.toMap(TextSupplier::getText, Enum::name));
                TEXTS.put(clazz, map);
            }
            name = map.get(text);
        }
        if (name != null) {
            return Enum.valueOf(clazz, name);
        }
        throw new IllegalArgumentException(
            String.format("Unknown text '%s' for '%s'", text, clazz));
    }

    /**
     * Get Enum from the title
     */
    public static <E extends Enum<E> & TitleSupplier> E deserializeTitle(@Nonnull String title,
        Class<E> clazz, boolean ignoreCase) {
        String name;
        if (ignoreCase) {
            Map<String, String> map = TITLES_IGNORE_CASE.get(clazz);
            if (map == null) {
                map = Arrays.stream(clazz.getEnumConstants())
                    .collect(Collectors.toMap(
                        e -> e.getTitle().toUpperCase(Locale.ROOT), Enum::name
                    ));
                TITLES_IGNORE_CASE.put(clazz, map);
            }
            name = map.get(title.toUpperCase(Locale.ROOT));
        } else {
            Map<String, String> map = TITLES.get(clazz);
            if (map == null) {
                map = Arrays.stream(clazz.getEnumConstants())
                    .collect(Collectors.toMap(TitleSupplier::getTitle, Enum::name));
                TITLES.put(clazz, map);
            }
            name = map.get(title);
        }
        if (name != null) {
            return Enum.valueOf(clazz, name);
        }
        throw new IllegalArgumentException(
            String.format("Unknown title '%s' for '%s'", title, clazz));
    }
}
