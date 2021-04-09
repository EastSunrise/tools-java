package wsg.tools.internet.base.support;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import wsg.tools.internet.base.Loggable;
import wsg.tools.internet.base.WrappedResponseHandler;
import wsg.tools.internet.common.UnexpectedException;

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
        this(name, host, defaultResponseHandler());
    }

    protected AbstractLoggableSite(String name, HttpHost host,
        WrappedResponseHandler<String> defaultHandler) {
        this(name, host, defaultClient(), defaultHandler,
            DEFAULT_PERMITS_PER_SECOND, DEFAULT_PERMITS_PER_SECOND);
    }

    protected AbstractLoggableSite(String name, HttpHost host, CloseableHttpClient client,
        WrappedResponseHandler<String> defaultHandler, double permitsPerSecond,
        double postPermitsPerSecond) {
        super(name, host, client, loadContext(name, host), defaultHandler, permitsPerSecond,
            postPermitsPerSecond);
    }

    @Nonnull
    protected static HttpClientContext loadContext(String name, @Nonnull HttpHost host) {
        HttpClientContext context = defaultContext();
        String filepath = name + "#" + host.toURI() + ".cookie";
        File file = new File(StringUtils.joinWith(File.separator, TMPDIR, "context", filepath));
        if (file.canRead()) {
            try (ObjectInputStream stream = new ObjectInputStream(
                FileUtils.openInputStream(file))) {
                log.info("Read cookies from {}.", file.getPath());
                CookieStore cookieStore = (CookieStore) stream.readObject();
                context.setCookieStore(cookieStore);
            } catch (IOException | ClassNotFoundException e) {
                throw new UnexpectedException(e);
            }
        }
        return context;
    }

    @Override
    public <T> T execute(@Nonnull HttpHost target, @Nonnull HttpUriRequest request,
        @Nonnull ResponseHandler<? extends T> handler) throws HttpResponseException {
        T entity = super.execute(target, request, handler);
        String filepath = httpGet("").filepath() + ".cookie";
        File file = new File(StringUtils.joinWith(File.separator, TMPDIR, "context", filepath));
        try (ObjectOutputStream stream = new ObjectOutputStream(FileUtils.openOutputStream(file))) {
            log.info("Synchronize cookies of {}.", getHost());
            stream.writeObject(getContext().getCookieStore());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return entity;
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
