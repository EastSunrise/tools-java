package wsg.tools.internet.base.exception;

/**
 * Exception when logging in a site.
 *
 * @author Kingen
 * @since 2020/9/22
 */
public class LoginException extends RuntimeException {

    public LoginException(String message) {
        super(message);
    }
}
