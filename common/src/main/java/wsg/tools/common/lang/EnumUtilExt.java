package wsg.tools.common.lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import wsg.tools.common.Constants;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.function.AliasSupplier;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Utility for Java enums.
 *
 * @author Kingen
 * @see org.apache.commons.lang3.EnumUtils
 * @since 2020/6/17
 */
public final class EnumUtilExt {

    private static final Map<Class<?>, Map<?, String>> KEYS = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> NAMES = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> CODES = new HashMap<>(1);
    private static final Map<Class<?>, Map<Integer, String>> INT_CODES = new HashMap<>(1);
    private static final Map<Class<?>, Map<String, String>> ALIAS = new HashMap<>(1);

    private EnumUtilExt() {
    }

    /**
     * Returns the enum constant of the specified enum type with the specified key.
     */
    @Nonnull
    public static <K, E extends Enum<E>>
    E valueOfKey(@Nonnull Class<E> clazz, @Nonnull K key, Function<E, K> keyMapper) {
        Map<?, String> map = KEYS.get(clazz);
        if (null == map) {
            map = Arrays.stream(clazz.getEnumConstants())
                .collect(Collectors.toMap(keyMapper, Enum::name));
            KEYS.put(clazz, map);
        }
        String name = map.get(key);
        if (null != name) {
            return Enum.valueOf(clazz, name);
        }
        throw new IllegalArgumentException(String.format("Unknown key '%s' for '%s'", key, clazz));
    }

    /**
     * Returns the enum constant of the specified enum type with the specified ignore-case name.
     * This method is a special case of {@link #valueOfKey(Class, Object, Function)}.
     *
     * @see #valueOfKey(Class, Object, Function)
     */
    @Nonnull
    public static <E extends Enum<E>>
    E valueOfIgnoreCase(Class<E> clazz, @Nonnull String target) {
        Map<String, String> map = NAMES.get(clazz);
        if (null == map) {
            map = Arrays.stream(clazz.getEnumConstants())
                .collect(Collectors.toMap(e -> e.name().toUpperCase(Locale.ROOT), Enum::name));
            NAMES.put(clazz, map);
        }
        String name = map.get(target.toUpperCase(Locale.ROOT));
        if (null != name) {
            return Enum.valueOf(clazz, name);
        }
        throw new IllegalArgumentException(
            String.format("Unknown name '%s' for '%s'", target, clazz));
    }

    /**
     * Returns the enum constant of the specified enum type with the specified code. This method is
     * a special case of {@link #valueOfKey(Class, Object, Function)}.
     *
     * @see #valueOfKey(Class, Object, Function)
     * @see CodeSupplier
     */
    @Nonnull
    public static <E extends Enum<E> & CodeSupplier>
    E valueOfCode(Class<E> clazz, @Nonnull String code) {
        Map<String, String> map = CODES.get(clazz);
        if (null == map) {
            map = Arrays.stream(clazz.getEnumConstants())
                .collect(Collectors.toMap(CodeSupplier::getCode, Enum::name));
            CODES.put(clazz, map);
        }
        String name = map.get(code);
        if (null != name) {
            return Enum.valueOf(clazz, name);
        }
        throw new IllegalArgumentException(
            String.format("Unknown code '%s' for '%s'", code, clazz));
    }

    /**
     * Returns the enum constant of the specified enum type with the specified code. This method is
     * a special case of {@link #valueOfKey(Class, Object, Function)}.
     *
     * @see #valueOfKey(Class, Object, Function)
     * @see IntCodeSupplier
     */
    @Nonnull
    public static <E extends Enum<E> & IntCodeSupplier> E valueOfIntCode(Class<E> clazz, int code) {
        Map<Integer, String> map = INT_CODES.get(clazz);
        if (null == map) {
            map = Arrays.stream(clazz.getEnumConstants())
                .collect(Collectors.toMap(IntCodeSupplier::getCode, Enum::name));
            INT_CODES.put(clazz, map);
        }
        String name = map.get(code);
        if (null != name) {
            return Enum.valueOf(clazz, name);
        }
        throw new IllegalArgumentException(
            String.format("Unknown int code '%s' for '%s'", code, clazz));
    }

    /**
     * Returns the enum constant of the specified enum type with the specified alias.
     *
     * @see AliasSupplier
     */
    @Nonnull
    public static <E extends Enum<E> & AliasSupplier>
    E valueOfAlias(Class<E> clazz, @Nonnull String alias) {
        Map<String, String> map = ALIAS.get(clazz);
        if (null == map) {
            map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
            for (E e : clazz.getEnumConstants()) {
                for (String t : e.getAlias()) {
                    MapUtilsExt.putIfAbsent(map, t, e.name());
                }
            }
            ALIAS.put(clazz, map);
        }
        String name = map.get(alias);
        if (null != name) {
            return Enum.valueOf(clazz, name);
        }
        throw new IllegalArgumentException(
            String.format("Unknown alias '%s' for '%s'", alias, clazz));
    }
}
