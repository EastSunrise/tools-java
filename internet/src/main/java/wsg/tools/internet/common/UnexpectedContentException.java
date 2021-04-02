package wsg.tools.internet.common;

/**
 * Exceptions thrown when getting unexpected content.
 *
 * @author Kingen
 * @since 2020/9/24
 */
public class UnexpectedContentException extends RuntimeException {

    private static final long serialVersionUID = -4807529841430634898L;

    public UnexpectedContentException(String message) {
        super(message);
    }
}
