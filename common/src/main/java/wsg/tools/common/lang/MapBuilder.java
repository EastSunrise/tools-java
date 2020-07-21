package wsg.tools.common.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Builder for {@link Map}.
 *
 * @author Kingen
 * @since 2020/7/20
 */
public class MapBuilder<K, V> {

    private static final int DEFAULT_CAPACITY = 4;
    private final Map<K, V> map;

    protected MapBuilder(int capacity, Map<K, V> map) {
        this.map = Objects.requireNonNullElseGet(map, () -> new HashMap<>(capacity));
    }

    public static <K, V> MapBuilder<K, V> builder(Map<K, V> map) {
        return new MapBuilder<>(DEFAULT_CAPACITY, map);
    }

    public static <K, V> MapBuilder<K, V> builder() {
        return new MapBuilder<>(DEFAULT_CAPACITY, null);
    }

    public static <K, V> MapBuilder<K, V> builder(int capacity) {
        return new MapBuilder<>(capacity, null);
    }

    public Map<K, V> build() {
        return map;
    }

    public MapBuilder<K, V> put(K k, V v) {
        map.put(k, v);
        return this;
    }
}
