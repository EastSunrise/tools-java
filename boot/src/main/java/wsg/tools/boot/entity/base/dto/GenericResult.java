package wsg.tools.boot.entity.base.dto;

import lombok.Getter;

/**
 * Generic result with an object of data.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Getter
public class GenericResult<T> extends Result {

    private T record;

    /**
     * Instantiate a successful result.
     */
    public GenericResult(T record) {
        super();
        this.record = record;
    }

    /**
     * Instantiate a failed result.
     */
    public GenericResult(String message) {
        super(message);
    }

    /**
     * Instantiate a failed result with args.
     */
    public GenericResult(String message, Object[] args) {
        super(message, args);
    }

    /**
     * Instantiate a failed result from an {@link Exception}.
     */
    public GenericResult(Exception e) {
        super(e);
    }

    public GenericResult(GenericResult<T> other) {
        super(other);
        this.record = other.record;
    }
}
