package wsg.tools.boot.pojo.result;

import lombok.Getter;
import wsg.tools.boot.pojo.base.Result;

import java.util.Map;

/**
 * Result when handle multi-data.
 *
 * @author Kingen
 * @since 2020/8/6
 */
@Getter
public class BatchResult<T> extends Result {

    private final int total;
    private final int count;
    private final Map<T, String> fails;

    /**
     * Instantiate a successful result.
     */
    public BatchResult(int count, Map<T, String> fails) {
        super();
        this.count = count;
        this.fails = fails;
        this.total = count + fails.size();
    }
}
