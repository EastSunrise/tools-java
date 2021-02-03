package wsg.tools.boot.pojo.result;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Result when handle multi-data.
 *
 * @author Kingen
 * @since 2020/8/6
 */
@Getter
public class BatchResult<T> extends BaseResult {

    private final int success;
    private final Map<T, String> fails;

    /**
     * Instantiate a successful result.
     */
    public BatchResult(int success, Map<T, String> fails) {
        super();
        this.success = success;
        this.fails = fails;
    }

    /**
     * Obtains an instance of empty results.
     */
    public static <V> BatchResult<V> empty() {
        return new BatchResult<>(0, Collections.emptyMap());
    }

    /**
     * Merges the given result and this one to a new result.
     *
     * @param result result to merge
     */
    public BatchResult<T> plus(BatchResult<T> result) {
        HashMap<T, String> map = new HashMap<>(this.fails);
        map.putAll(result.fails);
        return new BatchResult<>(this.success + result.success, map);
    }
}
