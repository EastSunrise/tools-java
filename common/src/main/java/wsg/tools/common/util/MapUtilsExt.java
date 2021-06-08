package wsg.tools.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility for {@link Map}.
 *
 * @author Kingen
 * @since 2021/3/6
 */
public final class MapUtilsExt {

    private MapUtilsExt() {
    }

    /**
     * Puts the key-value into the map if the key is absent, otherwise throw an exception.
     *
     * @throws IllegalStateException if the key is present in the map
     */
    public static <K, V> void putIfAbsent(Map<K, V> map, K key, V value) {
        V prev = map.putIfAbsent(key, value);
        if (null != prev) {
            String format = String
                .format("Duplicate key %s (attempted merging values %s and %s)", key, prev, value);
            throw new IllegalStateException(format);
        }
    }

    /**
     * Obtains a list of strings from the given map and remove the key.
     */
    @Nonnull
    public static List<String> getStringList(@Nonnull Map<String, String> map,
        String separatorChars, String... keys) {
        return getValues(map, Function.identity(), separatorChars, keys);
    }

    /**
     * Obtains a list of values from the given map and remove the key.
     *
     * @param separatorChars chars to split the string
     * @return list of values, ignoring {@literal null}
     */
    @Nonnull
    public static <T> List<T> getValues(@Nonnull Map<String, String> map,
        @Nonnull Function<? super String, ? extends T> function, String separatorChars,
        String... keys) {
        return Optional.ofNullable(getValue(map, s -> {
            List<T> list = new ArrayList<>(1);
            for (String part : StringUtils.split(s, separatorChars)) {
                CollectionUtils.addIgnoreNull(list, function.apply(part));
            }
            return list;
        }, keys)).orElse(new ArrayList<>(0));
    }

    /**
     * Obtains a value matched the given pattern from the given map and remove the key.
     */
    public static <T> T getValueIfMatched(Map<String, String> map, Pattern pattern,
        Function<? super Matcher, T> function, String... keys) {
        return getValue(map, text -> {
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                return function.apply(matcher);
            }
            return null;
        }, keys);
    }

    /**
     * Obtains a string from the given map and remove the key.
     */
    public static String getString(@Nonnull Map<String, String> map, String... keys) {
        return getValue(map, Function.identity(), keys);
    }

    /**
     * Obtains a specific value from the given map and remove the key.
     *
     * @param map      the map to query
     * @param function function transfer a string to the target object
     * @param keys     keys to query the map
     * @param <T>      type of returned value
     * @return target value, or {@literal null} if none key is found in the map
     */
    public static <T> T getValue(@Nonnull Map<String, String> map,
        @Nonnull Function<? super String, T> function, String... keys) {
        for (String key : keys) {
            String value = map.remove(key);
            if (StringUtils.isNotBlank(value)) {
                return function.apply(value);
            }
        }
        return null;
    }
}
