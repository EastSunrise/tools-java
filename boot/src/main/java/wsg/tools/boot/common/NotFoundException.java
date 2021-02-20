package wsg.tools.boot.common;

/**
 * Exceptions thrown when the target is not found.
 *
 * @author Kingen
 * @since 2021/2/20
 */
public class NotFoundException extends Exception {

    public NotFoundException(String message) {
        super(message);
    }
}
