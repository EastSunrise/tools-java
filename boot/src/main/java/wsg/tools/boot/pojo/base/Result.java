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

    protected final boolean success;
    protected Map<String, Object> args;
    private String message;

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
     * Instantiate a result from a known result.
     */
    protected Result(Result result) {
        this.success = result.success;
        this.message = result.message;
        this.args = result.args;
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
    public static Result fail(String format, Object... formatArgs) {
        return new Result(String.format(format, formatArgs));
    }

    /**
     * Obtain an instance of failed result from an {@link Exception}.
     */
    public static Result fail(Exception e) {
        return new Result(e);
    }

    /**
     * Obtain response of a successful result.
     */
    public static ResponseEntity<?> response() {
        return success().toResponse();
    }

    /**
     * Obtain response of a failed result.
     */
    public static ResponseEntity<?> response(String message, Object... formatArgs) {
        return fail(message, formatArgs).toResponse();
    }

    /**
     * Obtain response of a failed result from an {@link Exception}.
     */
    public static ResponseEntity<?> response(Exception e) {
        return fail(e).toResponse();
    }

    public Result put(String key, Object value) {
        if (!success) {
            throw new IllegalArgumentException("Failed result doesn't have args.");
        }
        args.put(key, value);
        return this;
    }

    public ResponseEntity<?> toResponse() {
        if (success) {
            return new ResponseEntity<>(args, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
