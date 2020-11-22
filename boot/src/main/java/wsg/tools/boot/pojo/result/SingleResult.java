package wsg.tools.boot.pojo.result;

import lombok.Getter;

import java.util.Objects;

/**
 * Result with a single not-null record.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Getter
public class SingleResult<T> extends BaseResult {

    private final T record;

    private SingleResult(T record) {
        super();
        this.record = Objects.requireNonNull(record);
    }

    /**
     * Obtains a successful instance of {@link SingleResult}.
     */
    public static <T> SingleResult<T> of(T data) {
        return new SingleResult<>(data);
    }
}
