package wsg.tools.boot.pojo.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import wsg.tools.boot.pojo.entity.base.FailureReason;

/**
 * Result when handle multi-data.
 *
 * @author Kingen
 * @since 2020/8/6
 */
@Slf4j
public class BatchResult<T> extends BaseResult {

    private final Map<T, String> failures;
    private int success;

    /**
     * Instantiate a successful result.
     */
    public BatchResult(int success, Map<T, String> failures) {
        super();
        this.success = success;
        this.failures = Objects.requireNonNull(failures);
    }

    /**
     * Obtains an instance of empty results.
     */
    @Nonnull
    @Contract(" -> new")
    public static <V> BatchResult<V> create() {
        return new BatchResult<>(0, new HashMap<>(4));
    }

    public int getSuccess() {
        return success;
    }

    public boolean hasFailures() {
        return !failures.isEmpty();
    }

    public Map<T, String> getFailures() {
        return failures;
    }

    /**
     * Merges the given result and this one to a new result.
     *
     * @param result result to merge
     */
    public BatchResult<T> plus(@Nonnull BatchResult<T> result) {
        HashMap<T, String> map = new HashMap<>(failures);
        map.putAll(result.failures);
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
        failures.put(t, reason);
    }

    /**
     * Adds a failure.
     */
    public void fail(@Nonnull T t, @Nonnull FailureReason reason) {
        failures.put(t, reason.getReason());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("BatchResult{")
            .append("success=").append(success).append("/").append(success + failures.size())
            .append("; ");
        if (hasFailures()) {
            builder.append("failures={");
            Map<String, List<T>> map = failures.entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue,
                    Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
            boolean first = true;
            for (Map.Entry<String, List<T>> entry : map.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    builder.append(",");
                }
                builder.append(entry.getKey()).append(":");
                List<T> values = entry.getValue();
                if (values.size() > 10) {
                    builder.append("(total ").append(values.size()).append(")");
                } else {
                    builder.append("{").append(StringUtils.join(values, ",")).append("}");
                }
            }
            builder.append("}");
        } else {
            builder.append("no failures");
        }
        builder.append("}");
        return builder.toString();
    }
}
