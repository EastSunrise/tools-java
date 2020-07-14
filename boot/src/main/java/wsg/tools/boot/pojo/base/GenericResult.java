package wsg.tools.boot.pojo.base;

import lombok.Getter;

/**
 * Generic result with an object of data.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Getter
public class GenericResult<T extends BaseDto> extends Result {

    private T data;

    public GenericResult(T data) {
        super();
        this.data = data;
    }

    public GenericResult(String message) {
        super(message);
    }

    public GenericResult(String message, Object[] args) {
        super(message, args);
    }

    public GenericResult(Exception e) {
        super(e);
    }

    public GenericResult(GenericResult<T> other) {
        super(other);
        this.data = other.data;
    }
}
