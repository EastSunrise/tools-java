package wsg.tools.internet.base;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;

import javax.annotation.Nullable;
import java.io.Closeable;

/**
 * A http session to execute requests under a domain.
 * <p>
 * {@link #getDomain()} appoints the target domain.
 * <p>
 * The core method is {@link #execute(RequestBuilder, ResponseHandler)} which executes the given request, handles the response
 * by the given handler and returns the response as an object of target type: request → response → {@code <T>}.
 * <p>
 * Method {@link #getContent} is an extension of {@link #execute}. It executes
 * the given request, handles the response as a String, handles the String by the given {@code ContentHandler}, and finally
 * returns the Sting as an object of target type: request → response → String → {@code T}.
 * <p>
 * todo concurrency of same session.
 *
 * @author Kingen
 * @since 2021/3/1
 */
public interface HttpSession extends Closeable {

    ResponseHandler<String> DEFAULT_RESPONSE_HANDLER = new BasicResponseHandler();

    /**
     * Obtains the domain of the session.
     *
     * @return the domain
     */
    String getDomain();

    /**
     * Executes the request and handle the response by the given handler.
     *
     * @param builder builder to construct a request
     * @param handler handler to generate an object from the {@link HttpResponse}.
     * @return entity from the response
     * @throws HttpResponseException if an error occurs when receive the response
     */
    <T> T execute(RequestBuilder builder, ResponseHandler<T> handler) throws HttpResponseException;

    /**
     * Obtains the content of the response of the request and returns it as an object of type {@code T} by the given handler.
     *
     * @param builder         builder to construct a request
     * @param responseHandler handler to generate a response String from a {@link HttpResponse}.
     * @param contentHandler  how to handle the content and return as an object of type {@code T}
     * @param strategy        the strategy of updating the snapshot
     * @return an object generated from the string response
     * @throws HttpResponseException if an error occurs when receive the response
     */
    <T> T getContent(RequestBuilder builder, ResponseHandler<String> responseHandler, ContentHandler<T> contentHandler, SnapshotStrategy strategy)
            throws HttpResponseException;

    /**
     * Creates a builder to construct a request of the given method under the sub domain.
     *
     * @param method    method of the request to construct
     * @param subDomain the sub domain that the request will access to
     * @return a builder to construct a request
     */
    RequestBuilder create(final String method, final String subDomain);

    /**
     * Obtains the cookie of the given name.
     *
     * @param name name of the cookie to query
     * @return value of the cookie, may null
     */
    @Nullable
    Cookie getCookie(String name);
}
