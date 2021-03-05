package wsg.tools.boot.pojo.result;

import java.util.Objects;
import lombok.Getter;

/**
 * Result with a single not-null record.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Getter
public final class SingleResult<T> extends BaseResult {

    private final T record;

    private SingleResult(T record) {
        super();
        this.record = Objects.requireNonNull(record);
    }

    /**
     * Obtains a successful instance.
     */
    public static <T> SingleResult<T> of(T data) {
        return new SingleResult<>(data);
    }
}
