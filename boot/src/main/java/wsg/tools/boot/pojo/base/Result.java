package wsg.tools.boot.pojo.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import wsg.tools.common.constant.Constants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Return common result with a message if failed.
 *
 * @author Kingen
 * @since 2020/6/26
 */
@Getter
public class Result implements Serializable {

    private final boolean success;
    private String message;
    private Map<String, Object> args;

    /**
     * Instantiate a successful result.
     */
    protected Result() {
        this(new HashMap<>(Constants.DEFAULT_MAP_CAPACITY));
    }

    /**
     * Instantiate a successful result with args.
     */
    protected Result(Map<String, Object> args) {
        this.success = true;
        this.args = args;
    }

    /**
     * Instantiate a failed result.
     */
    protected Result(String message) {
        this.success = false;
        this.message = message;
    }

    /**
     * Instantiate a failed result from an {@link Exception}.
     */
    protected Result(Exception e) {
        this(e.getMessage());
    }

    /**
     * Obtain an instance of successful result.
     */
    public static Result success() {
        return new Result();
    }

    /**
     * Obtain an instance of failed result.
     */
    public static Result fail(String message, Object... formatArgs) {
        return new Result(String.format(message, formatArgs));
    }

    /**
     * Obtain an instance of failed result from an {@link Exception}.
     */
    public static Result fail(Exception e) {
        return new Result(e);
    }

    /**
     * Return result of batch operation, including successful and error count.
     */
    public static Result batchResult(int total, int done) {
        Result result = new Result();
        result.put("total", total);
        result.put("done", done);
        result.put("error", total - done);
        return result;
    }

    public Object put(String key, Object value) {
        assert success;
        return args.put(key, value);
    }

    public ResponseEntity<?> toResponse() {
        if (success) {
            return new ResponseEntity<>(args, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
