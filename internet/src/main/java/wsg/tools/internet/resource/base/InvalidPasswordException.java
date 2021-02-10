package wsg.tools.internet.resource.base;

import lombok.Getter;

/**
 * Exception thrown when a given password is invalid.
 *
 * @author Kingen
 * @since 2021/2/8
 */
public class InvalidPasswordException extends InvalidResourceException {

    @Getter
    private final String password;

    /**
     * If a required password is lacking.
     */
    public <T extends AbstractResource & PasswordProvider> InvalidPasswordException(Class<T> clazz, String title, String url) {
        super("A password is required for " + clazz.getName(), title, url);
        this.password = null;
    }

    /**
     * If the given password is invalid.
     */
    public <T extends AbstractResource & PasswordProvider> InvalidPasswordException(Class<T> clazz, String title, String url, String password) {
        super("Not a valid password for " + clazz.getName(), title, url);
        this.password = password;
    }
}
