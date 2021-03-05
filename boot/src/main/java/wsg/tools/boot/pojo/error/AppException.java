package wsg.tools.boot.pojo.error;

/**
 * General throwable exceptions when running.
 *
 * @author Kingen
 * @since 2020/7/25
 */
public class AppException extends RuntimeException {

    private static final long serialVersionUID = 1670342063938140164L;

    public AppException(Throwable e) {
        super(e.getMessage(), e);
    }

    public AppException(String message) {
        super(message);
    }
}
