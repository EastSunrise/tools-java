package wsg.tools.boot.pojo.base;

import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Return generic result with an object of data.
 *
 * @author Kingen
 * @since 2020/6/30
 */
public class GenericResult<T> extends Result {

    private final T record;

    protected GenericResult(T record) {
        super();
        this.record = Objects.requireNonNull(record);
    }

    public GenericResult(String format, Object... formatArgs) {
        super(String.format(format, formatArgs));
        this.record = null;
    }

    public GenericResult(Exception e) {
        super(e);
        this.record = null;
    }

    /**
     * Obtains a successful instance of {@link GenericResult}.
     */
    public static <T> GenericResult<T> of(T data) {
        return new GenericResult<>(data);
    }

    public T get() {
        if (!isSuccess()) {
            throw new IllegalArgumentException("No value present.");
        }
        return record;
    }

    public void ifPresentOrLog(Consumer<? super T> action, Logger logger) {
        if (isSuccess()) {
            action.accept(record);
        } else {
            logger.error(error());
        }
    }

    public void ifPresentOr(Consumer<? super T> action, Consumer<String> errorAction) {
        if (isSuccess()) {
            action.accept(record);
        } else {
            errorAction.accept(error());
        }
    }

    public void ifPresentOrThrows(Consumer<? super T> action) {
        action.accept(get());
    }

    public T orElse(T other) {
        if (isSuccess()) {
            return record;
        }
        return other;
    }
}
