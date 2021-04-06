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
import org.apache.http.cookie.Cookie;
import wsg.tools.internet.base.Loggable;
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
        super(name, host);
        File file = cookieFile();
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
    }

    @Override
    public <T> T execute(@Nonnull HttpHost target, @Nonnull HttpUriRequest request,
        @Nonnull ResponseHandler<T> handler) throws HttpResponseException {
        T entity = super.execute(target, request, handler);
        try (ObjectOutputStream stream = new ObjectOutputStream(
            FileUtils.openOutputStream(cookieFile()))) {
            log.info("Synchronize cookies of {}.", getHost());
            stream.writeObject(context.getCookieStore());
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
        CookieStore cookieStore = context.getCookieStore();
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

    private File cookieFile() {
        String filepath = httpGet("").filepath() + ".cookie";
        return new File(StringUtils.joinWith(File.separator, TMPDIR, "context", filepath));
    }
}
