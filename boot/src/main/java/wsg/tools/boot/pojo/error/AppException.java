package wsg.tools.boot.pojo.error;

/**
 * General throwable exceptions when running.
 *
 * @author Kingen
 * @since 2020/7/25
 */
public class AppException extends RuntimeException {
    public AppException(Throwable e) {
        super(e.getMessage(), e);
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(String format, Object... args) {
        super(String.format(format, args));
    }
}
