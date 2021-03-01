package wsg.tools.internet.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.jsoup.nodes.Document;
import wsg.tools.internet.common.ContentHandlers;
import wsg.tools.internet.common.JsonHandler;
import wsg.tools.internet.common.Scheme;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

/**
 * Base class of a repository.
 * <p>
 * Method {@link #getDocument} is to obtain the html content and return as a {@link Document}.
 * Methods {@link #getObject} are to obtain the json content and return as a given Java object.
 *
 * @author Kingen
 * @since 2021/2/28
 */
public class RepositoryImpl implements HttpSession, Closeable {

    @Getter
    private final String name;
    private final HttpSession session;
    private final ResponseHandler<String> handler;

    protected RepositoryImpl(String name, BasicHttpSession session, ResponseHandler<String> handler) {
        this.name = name;
        this.session = Objects.requireNonNull(session, "Session may not be null.");
        this.handler = Objects.requireNonNull(handler, "ResponseHandler may not be null.");
    }

    protected RepositoryImpl(String name, String domain) {
        this(name, domain, DEFAULT_RESPONSE_HANDLER);
    }

    protected RepositoryImpl(String name, String domain, ResponseHandler<String> handler) {
        this(name, new BasicHttpSession(domain), handler);
    }

    protected RepositoryImpl(String name, Scheme scheme, String domain) {
        this(name, scheme, domain, DEFAULT_RESPONSE_HANDLER);
    }

    protected RepositoryImpl(String name, Scheme scheme, String domain, ResponseHandler<String> handler) {
        this(name, new BasicHttpSession(scheme, domain), handler);
    }

    /**
     * Return the html content of the response as a {code Document}.
     */
    public final Document getDocument(RequestBuilder builder, SnapshotStrategy strategy) throws HttpResponseException {
        return getContent(builder, handler, ContentHandlers.DOCUMENT_CONTENT_HANDLER, strategy);
    }

    /**
     * Return the json content of the response as a Java object.
     */
    public final <T> T getObject(RequestBuilder builder, ObjectMapper mapper, Class<T> clazz, SnapshotStrategy strategy) throws HttpResponseException {
        return getContent(builder, handler, new JsonHandler<>(mapper, clazz), strategy);
    }

    /**
     * Return the json content of the response as a generic Java object.
     */
    public final <T> T getObject(RequestBuilder builder, ObjectMapper mapper, TypeReference<T> type, SnapshotStrategy strategy) throws HttpResponseException {
        return getContent(builder, handler, new JsonHandler<>(mapper, type), strategy);
    }

    @Override
    public String getDomain() {
        return session.getDomain();
    }

    /**
     * Executes the request and handle the response by the given handler.
     *
     * @return entity from the response
     */
    @Override
    public final <T> T execute(RequestBuilder builder, ResponseHandler<T> handler) throws HttpResponseException {
        return session.execute(builder, handler);
    }

    @Override
    public <T> T getContent(RequestBuilder builder, ResponseHandler<String> responseHandler, ContentHandler<T> contentHandler, SnapshotStrategy strategy) throws HttpResponseException {
        return session.getContent(builder, responseHandler, contentHandler, strategy);
    }

    /**
     * Creates a builder to construct a get request.
     */
    protected final RequestBuilder builder0(String path, Object... args) {
        return builder("www", path, args);
    }

    /**
     * Creates a builder to construct a get request under the sub domain.
     */
    protected final RequestBuilder builder(String subDomain, String path, Object... args) {
        return create(HttpGet.METHOD_NAME, subDomain, path, args);
    }

    /**
     * Creates a builder to construct a request of the given method under the sub domain.
     */
    protected final RequestBuilder create(String method, String subDomain, String path, Object... args) {
        return create(method, subDomain).setPath(path, args);
    }

    @Override
    public RequestBuilder create(String method, String subDomain) {
        return session.create(method, subDomain);
    }

    @Nullable
    @Override
    public Cookie getCookie(String name) {
        return session.getCookie(name);
    }

    @Override
    public void close() throws IOException {
        session.close();
    }
}
