package wsg.tools.boot.entity.base.dto;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Common result used between layers.
 *
 * @author Kingen
 * @since 2020/6/26
 */
@Getter
public class Result implements Serializable {

    private boolean success;
    private String message;
    private Object[] args;

    /**
     * Instantiate a successful result.
     */
    public Result() {
        this.success = true;
    }

    /**
     * Instantiate a failed result.
     */
    public Result(String message) {
        this(message, null);
    }

    /**
     * Instantiate a failed result with args.
     */
    public Result(String message, Object[] args) {
        this.success = false;
        this.message = message;
        this.args = args;
    }

    /**
     * Instantiate a failed result from an {@link Exception}.
     */
    public Result(Exception e) {
        Objects.requireNonNull(e);
        this.success = false;
        this.message = e.getMessage();
    }

    /**
     * Instantiate a result from a known result.
     */
    public Result(Result other) {
        Objects.requireNonNull(other);
        this.success = other.success;
        this.message = other.message;
        this.args = other.args;
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
    public static Result fail(String message, Object... args) {
        return new Result(String.format(message, args));
    }

    /**
     * Obtain an instance of failed result.
     */
    public static Result fail(String message, Object[] args, Object... formatArgs) {
        return new Result(String.format(message, formatArgs), args);
    }

    /**
     * Obtain an instance of failed result from an {@link Exception}.
     */
    public static Result fail(Exception e) {
        return new Result(e);
    }
}
