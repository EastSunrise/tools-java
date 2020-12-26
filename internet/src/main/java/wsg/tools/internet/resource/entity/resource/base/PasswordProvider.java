package wsg.tools.internet.resource.entity.resource.base;

/**
 * Provide a password for the resource if necessary.
 *
 * @author Kingen
 * @since 2020/12/25
 */
public interface PasswordProvider {

    /**
     * Returns password of the resource.
     *
     * @return password
     */
    String getPassword();
}
