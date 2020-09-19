package wsg.tools.boot.pojo.base;

import lombok.Getter;

/**
 * Return generic result with an object of data.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Getter
public class GenericResult<T> extends Result {

    private final T data;

    protected GenericResult(T data) {
        super();
        this.data = data;
        put("data", data);
    }

    public GenericResult(String format, Object... formatArgs) {
        super(String.format(format, formatArgs));
        this.data = null;
    }

    public GenericResult(GenericResult<T> result) {
        super(result);
        this.data = result.data;
    }

    public GenericResult(Exception e) {
        super(e);
        this.data = null;
    }

    /**
     * Obtains a successful instance of {@link GenericResult}.
     */
    public static <T> GenericResult<T> of(T data) {
        return new GenericResult<>(data);
    }
}
