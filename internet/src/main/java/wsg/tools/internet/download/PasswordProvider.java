package wsg.tools.internet.download;

/**
 * Provides a password for the link if necessary.
 *
 * @author Kingen
 * @since 2020/12/25
 */
@FunctionalInterface
public interface PasswordProvider {

    /**
     * Returns the password of the link.
     *
     * @return the password
     */
    String getPassword();
}
