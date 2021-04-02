package wsg.tools.internet.download.support;

import wsg.tools.internet.download.PasswordProvider;

/**
 * Exception thrown when a given password is invalid.
 *
 * @author Kingen
 * @since 2021/2/8
 */
public class InvalidPasswordException extends InvalidResourceException {

    private static final long serialVersionUID = 1487936973372538799L;

    private final String password;

    /**
     * If a required password is lacking.
     */
    public <T extends AbstractLink & PasswordProvider> InvalidPasswordException(Class<T> clazz,
        String title, String url) {
        super("A password is required for " + clazz, title, url);
        this.password = null;
    }

    /**
     * If the given password is invalid.
     */
    public <T extends AbstractLink & PasswordProvider> InvalidPasswordException(Class<T> clazz,
        String title, String url, String password) {
        super("Not a valid password for " + clazz, title, url);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
