package wsg.tools.boot.pojo.result;

import lombok.Getter;
import wsg.tools.boot.pojo.base.Result;

import java.util.Map;

/**
 * Result when importing.
 *
 * @author Kingen
 * @since 2020/8/6
 */
@Getter
public class ImportResult extends Result {

    private int count;
    private Map<?, String> fails;

    /**
     * Instantiate a successful result.
     */
    public ImportResult(int count, Map<?, String> fails) {
        this.count = count;
        this.fails = fails;
        put("count", count);
        put("fails", fails);
        put("total", getTotal());
    }

    public ImportResult(Exception e) {
        super(e);
    }

    public int getTotal() {
        assert success;
        return count + fails.size();
    }
}
