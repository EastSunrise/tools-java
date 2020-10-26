package wsg.tools.internet.base.exception;

import java.io.IOException;

/**
 * Exception when logging in a site.
 *
 * @author Kingen
 * @since 2020/9/22
 */
public class LoginException extends IOException {

    public LoginException(String message) {
        super(message);
    }
}
