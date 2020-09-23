package wsg.tools.internet.base;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Indicate the site can log in.
 *
 * @param <U> type of user
 * @author Kingen
 * @since 2020/9/19
 */
interface Loggable<U> {

    /**
     * Log in the site with given username ana password.
     * Cookies are required to be update commonly.
     *
     * @param username username, not null
     * @param password password, not null
     * @throws IOException    exception when requesting
     * @throws LoginException if failed to log in
     */
    void login(String username, String password) throws IOException;

    /**
     * Obtains current user, null if not logon yet.
     *
     * @return object of user, may identity or username
     */
    @Nullable
    U user();
}
