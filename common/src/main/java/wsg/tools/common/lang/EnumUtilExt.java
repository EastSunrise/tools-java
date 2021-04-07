package wsg.tools.common.lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * Utility for Java enums.
 *
 * @author Kingen
 * @see org.apache.commons.lang3.EnumUtils
 * @since 2020/6/17
 */
public final class EnumUtilExt {

    private static final Map<Class<?>, Map<?, String>> CODES = new HashMap<>(1);
    private static final Map<Class<?>, Map<?, String>> KEYS = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> TEXTS = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> TEXTS_IGNORE_CASE = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> TITLES = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> TITLES_IGNORE_CASE = new HashMap<>(1);

    private EnumUtilExt() {
    }

    /**
     * Returns the enum constant of the specified enum type with the specified key.
     */
    @Nonnull
    public static <K, E extends Enum<E>>
    E valueOfKey(@Nonnull Class<E> clazz, @Nonnull K key, Function<E, K> keyMapper) {
        Map<?, String> map = KEYS.get(clazz);
        if (map == null) {
            map = Arrays.stream(clazz.getEnumConstants())
                .collect(Collectors.toMap(keyMapper, Enum::name));
            KEYS.put(clazz, map);
        }
        String name = map.get(key);
        if (name != null) {
            return Enum.valueOf(clazz, name);
        }
        throw new IllegalArgumentException(String.format("Unknown key '%s' for '%s'", key, clazz));
    }

    /**
     * Returns the enum constant of the specified enum type with the specified aka.
     *
     * @see AkaPredicate
     */
    @Nonnull
    public static <A, E extends Enum<E> & AkaPredicate<A>>
    E valueOfAka(@Nonnull Class<E> clazz, @Nonnull A other) {
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
     * Returns the enum constant of the specified enum type with the specified code. This method is
     * a special case of {@link #valueOfKey(Class, Object, Function)}.
     *
     * @see #valueOfKey(Class, Object, Function)
     * @see CodeSupplier
     */
    @Nonnull
    public static <C, E extends Enum<E> & CodeSupplier<C>>
    E valueOfCode(Class<E> clazz, @Nonnull C code) {
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
     * Returns the enum constant of the specified enum type with the specified text.
     *
     * @see TextSupplier
     */
    @Nonnull
    public static <E extends Enum<E> & TextSupplier>
    E valueOfText(Class<E> clazz, @Nonnull String text, boolean ignoreCase) {
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
     * Returns the enum constant of the specified enum type with the specified title.
     *
     * @see TitleSupplier
     */
    @Nonnull
    public static <E extends Enum<E> & TitleSupplier>
    E valueOfTitle(Class<E> clazz, @Nonnull String title, boolean ignoreCase) {
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
