package wsg.tools.internet.base;

/**
 * Indicates the site is loggable.
 *
 * @author Kingen
 * @since 2021/2/11
 */
@FunctionalInterface
public interface Loggable<T> {

    /**
     * Obtains current user.
     *
     * @return current user, an identity, username, or something else
     */
    T user();
}
