package wsg.tools.boot.pojo.error;

/**
 * Unexpected exceptions.
 * <p>
 * If this exception is thrown, it means there is an error within the codes.
 *
 * @author Kingen
 * @since 2020/11/22
 */
public class UnexpectedException extends RuntimeException {

    public UnexpectedException(Throwable cause) {
        super(cause);
    }
}
