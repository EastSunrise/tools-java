package wsg.tools.internet.common;

/**
 * Exceptions thrown when an unexpected exception which should never happen is thrown.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public class UnexpectedException extends RuntimeException {

    private static final long serialVersionUID = -5371713868818418420L;

    public UnexpectedException(Throwable cause) {
        super(cause);
    }
}
