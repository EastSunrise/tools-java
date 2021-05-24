package wsg.tools.internet.common;

/**
 * Exceptions thrown when logging in a site.
 *
 * @author Kingen
 * @since 2020/9/22
 */
public class LoginException extends Exception {

    private static final long serialVersionUID = 4200733299395702719L;

    public LoginException(String message) {
        super(message);
    }
}
