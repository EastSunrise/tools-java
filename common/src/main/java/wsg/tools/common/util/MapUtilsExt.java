package wsg.tools.common.util;

import java.util.Map;

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
        if (prev != null) {
            throw new IllegalStateException(String
                .format("Duplicate key %s (attempted merging values %s and %s)", key, prev, value));
        }
    }
}
