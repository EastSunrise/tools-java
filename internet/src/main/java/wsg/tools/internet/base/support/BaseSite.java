package wsg.tools.internet.base.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.jetbrains.annotations.Contract;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.ResponseWrapper;
import wsg.tools.internet.base.SiteClient;
import wsg.tools.internet.base.WrappedResponseHandler;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.SiteUtils;
import wsg.tools.internet.common.UnexpectedException;
import wsg.tools.internet.common.WrappedStringResponseHandler;

/**
 * A basic implementation of a site, providing a default context to execute requests.
 * <p>
 * todo rebuild the connection after a long interval.
 *
 * @author Kingen
 * @since 2021/2/28
 */
@Slf4j
public class BaseSite implements SiteClient, Closeable {

    protected static final int DEFAULT_TIME_OUT = 30000;
    protected static final String METHOD_GET = HttpGet.METHOD_NAME;
    protected static final String METHOD_POST = HttpPost.METHOD_NAME;

    private static final Header USER_AGENT = new BasicHeader(HTTP.USER_AGENT,
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/80.0.3987.132 Safari/537.36");

    private final String name;
    private final HttpHost host;
    private final CloseableHttpClient client;
    private final HttpClientContext context;

    protected BaseSite(String name, HttpHost host) {
        this(name, host, defaultClient(), defaultContext());
    }

    protected BaseSite(String name, HttpHost host, CloseableHttpClient client,
        HttpClientContext context) {
        SiteUtils.validateStatus(getClass());
        this.name = name;
        this.host = Objects.requireNonNull(host);
        this.client = Objects.requireNonNull(client);
        this.context = context;
    }

    @Nonnull
    @Contract("_ -> new")
    protected static HttpHost httpHost(String hostname) {
        return new HttpHost(hostname);
    }

    @Nonnull
    @Contract("_ -> new")
    protected static HttpHost httpsHost(String hostname) {
        return new HttpHost(hostname, -1, "https");
    }

    protected static CloseableHttpClient defaultClient() {
        return DecoratedHttpClientBuilder.create().setDefaultHeaders(List.of(USER_AGENT))
            .setConnectionManager(new PoolingHttpClientConnectionManager()).build();
    }

    @Nonnull
    protected static HttpClientContext defaultContext() {
        HttpClientContext clientContext = HttpClientContext.create();
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(DEFAULT_TIME_OUT)
            .setSocketTimeout(DEFAULT_TIME_OUT).build();
        clientContext.setRequestConfig(requestConfig);
        return clientContext;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public HttpHost getHost() {
        return host;
    }

    @Override
    public <T> T execute(@Nonnull HttpRequest request, ResponseHandler<? extends T> responseHandler)
        throws IOException {
        return client.execute(host, request, responseHandler, context);
    }

    @Override
    public <T> ResponseWrapper<T> getResponseWrapper(@Nonnull RequestBuilder builder,
        WrappedResponseHandler<T> responseHandler) throws IOException {
        return execute(builder.build(), responseHandler);
    }

    @Override
    public String getContent(@Nonnull RequestBuilder builder) throws IOException {
        WrappedStringResponseHandler handler = new WrappedStringResponseHandler();
        return getResponseWrapper(builder, handler).getContent();
    }

    /**
     * An extension of {@link #getContent} which returns the html content of the response as a
     * {@link Document} and splits the exception if thrown.
     *
     * @throws NotFoundException      if the target document is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     * @see #findDocument
     */
    public Document getDocument(@Nonnull RequestBuilder builder)
        throws NotFoundException, OtherResponseException {
        try {
            return Jsoup.parse(getContent(builder));
        } catch (HttpResponseException e) {
            throw SiteUtils.handleException(e);
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
    }

    /**
     * An extension of {@link #getContent} which returns the html content of the response as a
     * {@link Document}.
     * <p>
     * Different from the method {@link #getDocument}, this method will throw a runtime exception
     * instead of {@link NotFoundException} if the target document is not found.
     *
     * @throws OtherResponseException if an unexpected error occurs when requesting
     * @see #getDocument
     */
    public Document findDocument(RequestBuilder builder) throws OtherResponseException {
        try {
            return getDocument(builder);
        } catch (NotFoundException e) {
            throw new UnexpectedException(e);
        }
    }

    /**
     * An extension of {@link #getContent} which returns the json content of the response as a Java
     * object and splits the exception if thrown.
     *
     * @throws NotFoundException      if the target object is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    public <T> T getObject(@Nonnull RequestBuilder builder, @Nonnull ObjectMapper mapper,
        Class<? extends T> clazz) throws NotFoundException, OtherResponseException {
        try {
            return mapper.readValue(getContent(builder), clazz);
        } catch (HttpResponseException e) {
            throw SiteUtils.handleException(e);
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
    }

    /**
     * An extension of {@link #getContent} which returns the json content of the response as a
     * <i>generic</i> Java object and splits the exception if thrown.
     *
     * @throws NotFoundException      if the target object is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    public <T> T getObject(RequestBuilder builder, @Nonnull ObjectMapper mapper,
        TypeReference<? extends T> type) throws NotFoundException, OtherResponseException {
        try {
            return mapper.readValue(getContent(builder), type);
        } catch (HttpResponseException e) {
            throw SiteUtils.handleException(e);
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Returns a HttpGet request builder with the given path.
     *
     * @param args arguments to format the path
     */
    @Nonnull
    @Contract("_, _ -> new")
    protected final RequestBuilder httpGet(@Nonnull String path, Object... args) {
        return RequestBuilder.get(String.format(path, args));
    }

    /**
     * Creates a http request builder of the specified method under the specified substation.
     *
     * @param args arguments to format the path
     */
    protected final RequestBuilder create(String substation, String method, String path,
        Object... args) {
        HttpHost target = host;
        if (StringUtils.isNotBlank(substation)) {
            String hostname = substation + "." + host.getHostName();
            target = new HttpHost(hostname, host.getPort(), host.getSchemeName());
        }
        return RequestBuilder.create(method).setUri(target.toURI() + String.format(path, args));
    }

    protected final HttpClientContext getContext() {
        return context;
    }

    /**
     * Returns the cookie of the given name in current context.
     *
     * @param name name of the cookie to be queried
     * @return value of the cookie, may null
     */
    @Nullable
    protected final Cookie getCookie(String name) {
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
}
