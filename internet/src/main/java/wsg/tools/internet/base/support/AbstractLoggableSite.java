package wsg.tools.internet.base.support;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import wsg.tools.internet.base.Loggable;

/**
 * This class provides a skeletal implementation of a loggable site whose cookies that containing
 * logon information will be stored locally as a browser does.
 *
 * @author Kingen
 * @since 2021/4/6
 */
@Slf4j
public abstract class AbstractLoggableSite<U> extends BaseSite implements Loggable<U> {

    protected AbstractLoggableSite(String name, HttpHost host) {
        super(name, host);
    }

    protected AbstractLoggableSite(String name, HttpHost host, CloseableHttpClient client,
        @Nonnull HttpClientContext context) {
        super(name, host, client, context);
    }

    /**
     * Returns the cookie of the given name in current context.
     *
     * @param name name of the cookie to be queried
     * @return value of the cookie, may null
     */
    @Nullable
    protected Cookie getCookie(String name) {
        CookieStore cookieStore = getContext().getCookieStore();
        if (cookieStore == null) {
            return null;
        }
        for (Cookie cookie : cookieStore.getCookies()) {
            if (Objects.equals(cookie.getName(), name)) {
                return cookie;
            }
        }
        return null;
    }
}
