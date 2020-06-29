package wsg.tools.boot.entity.base;

import lombok.Getter;

/**
 * Common result used between layers.
 *
 * @author Kingen
 * @since 2020/6/26
 */
@Getter
public class Result {

    private boolean success;
    private String message;

    private Result(boolean success) {
        this.success = success;
    }

    /**
     * Obtain an instance of failed result.
     */
    public static Result fail(String message, Object... args) {
        Result result = new Result(false);
        result.message = String.format(message, args);
        return result;
    }

    public static Result success() {
        return new Result(true);
    }
}
