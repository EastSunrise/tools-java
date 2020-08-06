package wsg.tools.boot.pojo.result;

import lombok.Getter;
import wsg.tools.boot.pojo.base.Result;

import java.util.List;

/**
 * Result when importing.
 *
 * @author Kingen
 * @since 2020/8/6
 */
@Getter
public class ImportResult extends Result {

    private final int added;
    private final int exists;
    private final List<?> notFounds;

    /**
     * Instantiate a successful result.
     */
    public ImportResult(int added, int exists, List<?> notFounds) {
        this.added = added;
        this.exists = exists;
        this.notFounds = notFounds;
        put("added", added);
        put("exists", exists);
        put("not found", notFounds);
        put("total", getTotal());
    }

    public ImportResult(Exception e) {
        super(e);
        added = 0;
        exists = 0;
        notFounds = null;
    }

    public int getTotal() {
        assert success;
        return added + exists + notFounds.size();
    }
}
