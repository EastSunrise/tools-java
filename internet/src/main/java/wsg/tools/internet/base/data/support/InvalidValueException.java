package wsg.tools.internet.base.data.support;

/**
 * Exceptions thrown when a value is invalid.
 *
 * @author Kingen
 * @since 2021/3/31
 */
public class InvalidValueException extends Exception {

    private static final long serialVersionUID = -6632541504929469456L;

    public InvalidValueException(String message) {
        super(message);
    }
}
