package wsg.tools.boot.pojo.base;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

/**
 * Return generic result with an object of data.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Getter
public class GenericResult<T extends BaseDto> extends Result {

    private final T data;

    protected GenericResult(T data) {
        super();
        this.data = data;
    }

    /**
     * Obtains a successful instance of {@link GenericResult}.
     */
    public static <T extends BaseDto> GenericResult<T> of(T data) {
        return new GenericResult<>(data);
    }

    @Override
    public ResponseEntity<?> toResponse() {
        put("data", data);
        return super.toResponse();
    }
}
