package wsg.tools.boot.entity.base.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Result of batch operation.
 *
 * @author Kingen
 * @since 2020/7/10
 */
@Getter
@Setter
public class BatchResult extends Result {
    private int total;
    private int error;
    private int done;

    public BatchResult(Exception e) {
        super(e);
    }

    public BatchResult(String message) {
        super(message);
    }

    public BatchResult(int total, int done) {
        this.total = total;
        this.done = done;
    }
}
