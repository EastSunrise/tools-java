package wsg.tools.internet.base.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Contract;
import org.jsoup.nodes.Document;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.base.ContentHandler;
import wsg.tools.internet.base.HttpSession;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UnexpectedException;

/**
 * A Wrapper of a {@code HttpSession} and one or more related repositories.
 * <p>
 * Method {@link #getDocument} is to obtain the html content and return as a {@link Document}.
 * Methods {@link #getObject} are to obtain the json content and return as a given Java object.
 *
 * @author Kingen
 * @since 2021/2/28
 */
public class BaseSite implements HttpSession {

    protected static final String METHOD_GET = HttpGet.METHOD_NAME;
    protected static final String METHOD_POST = HttpPost.METHOD_NAME;

    protected static final ResponseHandler<String> DEFAULT_RESPONSE_HANDLER =
        new BasicResponseHandler() {
            @Override
            public String handleEntity(HttpEntity entity) throws IOException {
                return EntityUtils.toString(entity, Constants.UTF_8);
            }
        };

    @Getter
    private final String name;

    private final HttpSession session;

    private final ResponseHandler<String> defaultHandler;

    protected BaseSite(String name, HttpSession session) {
        this(name, session, DEFAULT_RESPONSE_HANDLER);
    }

    protected BaseSite(String name, HttpSession session, ResponseHandler<String> defaultHandler) {
        this.name = name;
        this.session = Objects.requireNonNull(session, "session");
        this.defaultHandler = Objects.requireNonNull(defaultHandler, "defaultHandler");
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
    public <T> T execute(RequestBuilder builder, ResponseHandler<T> handler)
        throws HttpResponseException {
        return session.execute(builder, handler);
    }

    @Override
    public <T> T getContent(RequestBuilder builder, ResponseHandler<String> responseHandler,
        ContentHandler<T> contentHandler, SnapshotStrategy<T> strategy)
        throws HttpResponseException {
        return session.getContent(builder, responseHandler, contentHandler, strategy);
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

    /**
     * Return the html content of the response as a {code Document}. Differ from the method {@link
     * #getDocument(RequestBuilder, SnapshotStrategy)}, this method will throw a runtime exception
     * instead of {@code NotFoundException} if the target document is not found.
     *
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    protected Document findDocument(RequestBuilder builder, SnapshotStrategy<Document> strategy)
        throws OtherResponseException {
        try {
            return getContent(builder, defaultHandler, new DocumentHandler(), strategy);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new UnexpectedException(e);
            }
            throw new OtherResponseException(e);
        }
    }

    /**
     * Return the html content of the response as a {code Document}.
     *
     * @throws NotFoundException      if the target document is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    public Document getDocument(RequestBuilder builder, SnapshotStrategy<Document> strategy)
        throws NotFoundException, OtherResponseException {
        try {
            return getContent(builder, defaultHandler, new DocumentHandler(), strategy);
        } catch (HttpResponseException e) {
            throw handleException(e);
        }
    }

    /**
     * Return the json content of the response as a Java object.
     *
     * @throws NotFoundException      if the target object is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    public <T> T getObject(RequestBuilder builder, ObjectMapper mapper, Class<T> clazz,
        SnapshotStrategy<T> strategy) throws NotFoundException, OtherResponseException {
        try {
            return getContent(builder, defaultHandler, new JsonHandler<>(mapper, clazz), strategy);
        } catch (HttpResponseException e) {
            throw handleException(e);
        }
    }

    /**
     * Return the json content of the response as a generic Java object.
     *
     * @throws NotFoundException      if the target object is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    public <T> T getObject(RequestBuilder builder, ObjectMapper mapper, TypeReference<T> type,
        SnapshotStrategy<T> strategy) throws NotFoundException, OtherResponseException {
        try {
            return getContent(builder, defaultHandler, new JsonHandler<>(mapper, type), strategy);
        } catch (HttpResponseException e) {
            throw handleException(e);
        }
    }

    @Nonnull
    @Contract("_ -> new")
    private OtherResponseException handleException(@Nonnull HttpResponseException e)
        throws NotFoundException, OtherResponseException {
        if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            throw new NotFoundException(e.getMessage());
        }
        return new OtherResponseException(e);
    }

    /**
     * Creates a builder to construct a get request.
     */
    protected RequestBuilder builder0(String path, Object... args) {
        return builder(null, path, args);
    }

    /**
     * Creates a builder to construct a get request under the sub domain.
     */
    protected RequestBuilder builder(String subDomain, String path, Object... args) {
        return create(METHOD_GET, subDomain).setPath(path, args);
    }
}
