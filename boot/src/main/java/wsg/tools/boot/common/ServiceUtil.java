package wsg.tools.boot.common;

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
    public static <T, R extends Result> BatchResult<T> batch(Iterable<T> data, Function<T, R> function) {
        Map<T, String> fails = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        int count = 0;
        for (T t : data) {
            R r = function.apply(t);
            if (r.isSuccess()) {
                count++;
            } else {
                fails.put(t, r.getMessage());
            }
        }
        return new BatchResult<>(count, fails);
    }
}
