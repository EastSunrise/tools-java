package wsg.tools.internet.base.exception;

/**
 * Exceptions thrown when getting unexpected content.
 *
 * @author Kingen
 * @since 2020/9/24
 */
public class UnexpectedContentException extends RuntimeException {

    public UnexpectedContentException(String message) {
        super(message);
    }
}
