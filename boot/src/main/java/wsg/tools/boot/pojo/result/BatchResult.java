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

    private int count;
    private Map<T, String> fails;

    /**
     * Instantiate a successful result.
     */
    public BatchResult(int count, Map<T, String> fails) {
        this.count = count;
        this.fails = fails;
        put("count", count);
        put("fails", fails);
        put("total", getTotal());
    }

    public BatchResult(Exception e) {
        super(e);
    }

    public int getTotal() {
        assert success;
        return count + fails.size();
    }
}
