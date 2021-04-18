package wsg.tools.boot.pojo.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import wsg.tools.boot.pojo.entity.base.FailureReason;

/**
 * Result when handle multi-data.
 *
 * @author Kingen
 * @since 2020/8/6
 */
@Slf4j
@Getter
public class BatchResult<T> extends BaseResult {

    private final Map<T, String> fails;
    private int success;

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
    @Nonnull
    @Contract(" -> new")
    public static <V> BatchResult<V> create() {
        return new BatchResult<>(0, new HashMap<>(4));
    }

    /**
     * Merges the given result and this one to a new result.
     *
     * @param result result to merge
     */
    public BatchResult<T> plus(@Nonnull BatchResult<T> result) {
        HashMap<T, String> map = new HashMap<>(fails);
        map.putAll(result.fails);
        return new BatchResult<>(success + result.success, map);
    }

    /**
     * Adds a success.
     */
    public void succeed() {
        success++;
    }

    /**
     * Adds a failure.
     */
    public void fail(@Nonnull T t, @Nonnull String reason) {
        fails.put(t, reason);
    }

    /**
     * Adds a failure.
     */
    public void fail(@Nonnull T t, @Nonnull FailureReason reason) {
        fails.put(t, reason.getText());
    }

    /**
     * Prints this result.
     */
    public void print(@Nonnull Function<T, String> toString) {
        log.info("Success: {}/{}", success, success + fails.size());
        if (fails.isEmpty()) {
            return;
        }
        Map<String, List<T>> map = fails.entrySet().stream()
            .collect(Collectors.groupingBy(Map.Entry::getValue,
                Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
        for (Map.Entry<String, List<T>> entry : map.entrySet()) {
            List<T> values = entry.getValue();
            if (values.size() > 10) {
                log.error("{}: (total {})", entry.getKey(), values.size());
            } else {
                log.error("{}: {}", entry.getKey(),
                    values.stream().map(toString).collect(Collectors.joining(",")));
            }
        }
    }
}
