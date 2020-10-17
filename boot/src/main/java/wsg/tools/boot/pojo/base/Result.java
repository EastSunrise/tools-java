package wsg.tools.boot.pojo.base;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Return common result with a message if failed.
 * todo 多继承实现
 *
 * @author Kingen
 * @since 2020/6/26
 */
public class Result implements Serializable {

    private final boolean success;
    private final String message;

    /**
     * Instantiate a successful result.
     */
    public Result() {
        this.success = true;
        this.message = null;
    }

    /**
     * Instantiate a failed result.
     */
    public Result(String message) {
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

    public final boolean isSuccess() {
        return success;
    }

    public String error() {
        if (isSuccess()) {
            throw new IllegalArgumentException("Not a failed result.");
        }
        return message;
    }

    public void ifSuccessOr(Runnable action, Consumer<String> errorAction) {
        if (isSuccess()) {
            action.run();
        } else {
            errorAction.accept(error());
        }
    }

    @Override
    public String toString() {
        if (success) {
            return "success";
        } else {
            return message;
        }
    }
}
