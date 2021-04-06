package wsg.tools.internet.base;

import java.io.Closeable;
import javax.annotation.Nullable;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;
import wsg.tools.internet.base.support.RequestBuilder;

/**
 * A http session to execute requests under a domain. It is similar to {@link HttpClient}, but
 * limited under a specified domain.
 * <p>
 * todo concurrency of sessions under the same domain.
 * <p>
 * todo implement with {@link HttpClient#execute(HttpHost, HttpRequest, ResponseHandler,
 * HttpContext)}
 *
 * @author Kingen
 * @since 2021/3/1
 */
public interface HttpSession extends Closeable {

    /**
     * Returns the domain of the session.
     *
     * @return the domain
     */
    String getDomain();

    /**
     * The core method that executes the given request, processes the response and returns it as an
     * object of target type.
     *
     * @param builder builder to construct a request
     * @param handler handler to generate an object from the response
     * @return object of the response
     * @throws HttpResponseException if an error occurs when requesting
     */
    <T> T execute(RequestBuilder builder, ResponseHandler<T> handler) throws HttpResponseException;

    /**
     * An extension of {@link #execute} which assumes that the content of the response is a {@code
     * String}. It generated a string from the response and then converts the string to an object of
     * target type.
     *
     * @param builder         builder to construct a request
     * @param responseHandler handler to generate a string from the response
     * @param contentHandler  how to convert the string of the content to an object of target type
     * @param strategy        the strategy of updating the snapshot
     * @return an object generated from the string response
     * @throws HttpResponseException if an error occurs when requesting
     */
    <T> T getContent(RequestBuilder builder, ResponseHandler<String> responseHandler,
        ContentHandler<T> contentHandler, SnapshotStrategy<T> strategy)
        throws HttpResponseException;

    /**
     * Creates a builder to construct a request of the given method under the sub domain.
     *
     * @param method    method of the request to be constructed
     * @param subDomain the sub domain that the request will access to
     * @return a builder to construct a request
     */
    RequestBuilder create(String method, String subDomain);

    /**
     * Returns the cookie of the given name in this session.
     *
     * @param name name of the cookie to be queried
     * @return value of the cookie, may null
     */
    @Nullable
    Cookie getCookie(String name);
}
