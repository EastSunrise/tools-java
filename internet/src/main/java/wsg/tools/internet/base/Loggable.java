package wsg.tools.internet.base;

/**
 * Indicates the site is loggable.
 *
 * @param <T> type of the users
 * @author Kingen
 * @since 2021/2/11
 */
public interface Loggable<T> {

    /**
     * Returns current logged-in user.
     *
     * @return current logged-in user, or {@literal null} if not logged in
     */
    T user();
}
