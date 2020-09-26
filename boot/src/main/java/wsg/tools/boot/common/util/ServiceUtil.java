package wsg.tools.boot.common.util;

import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.common.constant.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility for operations of service.
 *
 * @author Kingen
 * @since 2020/9/23
 */
public final class ServiceUtil {

    /**
     * Execute batch operations.
     */
    public static <T, R extends Result> BatchResult<T> batch(Iterable<T> data, Function<T, R> action) {
        return batch(data, action, t -> t);
    }

    /**
     * Execute batch operations.
     */
    public static <T, R extends Result, K> BatchResult<K> batch(Iterable<T> data, Function<T, R> action, Function<T, K> keyAction) {
        Map<K, String> fails = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        int count = 0;
        for (T t : data) {
            R r = action.apply(t);
            if (r.isSuccess()) {
                count++;
            } else {
                fails.put(keyAction.apply(t), r.error());
            }
        }
        return new BatchResult<>(count, fails);
    }
}
