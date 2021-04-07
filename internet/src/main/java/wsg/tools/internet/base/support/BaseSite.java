package wsg.tools.internet.base.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.jetbrains.annotations.Contract;
import org.jsoup.nodes.Document;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.base.ContentHandler;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.SiteUtils;
import wsg.tools.internet.common.StringResponseHandler;
import wsg.tools.internet.common.UnexpectedException;

/**
 * A basic implementation of a site, providing a default context to execute requests.
 *
 * @author Kingen
 * @since 2021/2/28
 */
@Slf4j
@SuppressWarnings("UnstableApiUsage")
public class BaseSite implements Closeable {

    protected static final int DEFAULT_TIME_OUT = 30000;
    protected static final double DEFAULT_PERMITS_PER_SECOND = 10.0;
    protected static final String METHOD_GET = HttpGet.METHOD_NAME;
    protected static final String METHOD_POST = HttpPost.METHOD_NAME;
    protected static final String TMPDIR = Constants.SYSTEM_TMPDIR + "tools";
    private static final Header USER_AGENT = new BasicHeader(HTTP.USER_AGENT,
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/80.0.3987.132 Safari/537.36");

    private final String name;
    private final HttpHost host;
    private final Map<String, RateLimiter> limiters;
    private final CloseableHttpClient client;
    private final HttpClientContext context;
    private final ResponseHandler<String> defaultHandler;

    protected BaseSite(String name, HttpHost host) {
        this(name, host, defaultResponseHandler());
    }

    protected BaseSite(String name, HttpHost host, ResponseHandler<String> defaultHandler) {
        this(name, host, defaultClient(), defaultContext(), defaultHandler,
            DEFAULT_PERMITS_PER_SECOND, DEFAULT_PERMITS_PER_SECOND);
    }

    protected BaseSite(String name, HttpHost host, CloseableHttpClient client,
        HttpClientContext context, ResponseHandler<String> defaultHandler, double permitsPerSecond,
        double postPermitsPerSecond) {
        SiteUtils.validateStatus(getClass());
        this.name = name;
        this.host = Objects.requireNonNull(host);
        this.limiters = new HashMap<>(2);
        limiters.put(HttpGet.METHOD_NAME, RateLimiter.create(permitsPerSecond));
        limiters.put(HttpPost.METHOD_NAME, RateLimiter.create(postPermitsPerSecond));
        this.client = client;
        this.context = context;
        this.defaultHandler = Objects.requireNonNull(defaultHandler, "defaultHandler");
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
        return HttpClientBuilder.create().setDefaultHeaders(List.of(USER_AGENT))
            .setConnectionManager(new PoolingHttpClientConnectionManager()).build();
    }

    @Nonnull
    protected static HttpClientContext defaultContext() {
        HttpClientContext context = HttpClientContext.create();
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(DEFAULT_TIME_OUT)
            .setSocketTimeout(DEFAULT_TIME_OUT).build();
        context.setRequestConfig(requestConfig);
        return context;
    }

    @Nonnull
    @Contract(" -> new")
    protected static ResponseHandler<String> defaultResponseHandler() {
        return new StringResponseHandler();
    }

    protected CloseableHttpClient getClient() {
        return client;
    }

    protected HttpClientContext getContext() {
        return context;
    }

    /**
     * Returns a HttpGet request wrapper with the given path.
     *
     * @param args arguments to format the path
     */
    @Nonnull
    @Contract("_, _ -> new")
    protected RequestWrapper httpGet(@Nonnull String path, Object... args) {
        return new RequestWrapper(RequestBuilder.get(String.format(path, args)), host);
    }

    /**
     * Returns a HttpPost request wrapper with the given path.
     *
     * @param args arguments to format the path
     */
    @Nonnull
    @Contract("_, _ -> new")
    protected RequestWrapper httpPost(String path, Object... args) {
        return new RequestWrapper(RequestBuilder.post(String.format(path, args)), host);
    }

    /**
     * Creates a http request wrapper of the specified method under the specified substation.
     *
     * @param args arguments to format the path
     */
    protected RequestWrapper create(String substation, String method, String path, Object... args) {
        HttpHost target = host;
        if (StringUtils.isNotBlank(substation)) {
            String hostname = substation + "." + host.getHostName();
            target = new HttpHost(hostname, host.getPort(), host.getSchemeName());
        }
        RequestBuilder builder = RequestBuilder.create(method).setUri(String.format(path, args));
        return new RequestWrapper(builder, target);
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the host of the site.
     *
     * @return the host
     */
    public HttpHost getHost() {
        return host;
    }

    /**
     * Returns the host name.
     *
     * @return the host name (IP or DNS name)
     */
    public String getHostname() {
        return host.getHostName();
    }

    /**
     * The core method that executes the given request, processes the response and returns it as an
     * object of target type.
     *
     * @param request the request to be executed
     * @param handler handler to generate an object from the response
     * @param context the context used for the execution
     * @return the response object as generated by the response handler
     * @throws HttpResponseException if an error occurs when requesting
     * @see HttpClient#execute(HttpHost, HttpRequest, ResponseHandler, HttpContext)
     */
    public <T> T execute(@Nonnull HttpHost target, @Nonnull HttpUriRequest request,
        @Nonnull ResponseHandler<? extends T> handler, HttpContext context)
        throws HttpResponseException {
        URI uri = URI.create(target.toURI()).resolve(request.getURI());
        log.info("{} from {}", request.getMethod(), uri);
        limiters.get(request.getMethod()).acquire();
        try {
            return client.execute(target, request, handler, context);
        } catch (HttpResponseException e) {
            throw e;
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
    }

    /**
     * Executes the request with current context.
     *
     * @see #execute(HttpHost, HttpUriRequest, ResponseHandler, HttpContext)
     */
    public <T> T execute(@Nonnull HttpHost target, @Nonnull HttpUriRequest request,
        @Nonnull ResponseHandler<? extends T> handler) throws HttpResponseException {
        return execute(target, request, handler, context);
    }

    /**
     * An extension of {@link #execute} which assumes that the content of the response is a {@code
     * String}. It generated a string from the response and then converts the string to an object of
     * target type.
     *
     * @param wrapper         the wrapper containing the request to be executed
     * @param responseHandler handler to generate a string from the response
     * @param contentHandler  how to convert the string of the content to an object of target type
     * @param strategy        the strategy of updating the snapshot
     * @return an object generated from the string response
     * @throws HttpResponseException if an error occurs when requesting
     */
    public <T> T getContent(@Nonnull RequestWrapper wrapper,
        ResponseHandler<String> responseHandler,
        @Nonnull ContentHandler<? extends T> contentHandler, SnapshotStrategy<T> strategy)
        throws HttpResponseException {
        String filepath = wrapper.filepath();
        filepath += Constants.EXTENSION_SEPARATOR + contentHandler.extension();
        File file = new File(TMPDIR + filepath);

        String content;
        if (!file.isFile()) {
            content = updateSnapshot(wrapper, responseHandler, file);
            return contentHandler.handleContent(content);
        }
        log.info("Read from {}", file.getPath());
        try {
            content = FileUtils.readFileToString(file, Constants.UTF_8);
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
        T t = contentHandler.handleContent(content);
        if (strategy.ifUpdate(t)) {
            content = updateSnapshot(wrapper, responseHandler, file);
            return contentHandler.handleContent(content);
        }
        return t;
    }

    /**
     * Executes the request, handle the response, return it as a String, and write to the file as a
     * snapshot.
     */
    private String updateSnapshot(RequestWrapper wrapper, ResponseHandler<String> handler,
        File file)
        throws HttpResponseException {
        HttpHost target = wrapper.getHttpHost();
        if (target == null) {
            target = host;
        }
        String content = execute(target, wrapper.build(), handler);
        try {
            FileUtils.write(file, content, Constants.UTF_8);
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
        return content;
    }

    /**
     * An extension of {@link #getContent} which returns the html content of the response as a
     * {@code Document} and splits the exception if thrown.
     *
     * @throws NotFoundException      if the target document is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     * @see #findDocument
     */
    public Document getDocument(RequestWrapper wrapper, SnapshotStrategy<Document> strategy)
        throws NotFoundException, OtherResponseException {
        try {
            return getContent(wrapper, defaultHandler, new DocumentHandler(), strategy);
        } catch (HttpResponseException e) {
            throw handleException(e);
        }
    }

    /**
     * An extension of {@link #getContent} which returns the html content of the response as a
     * {@code Document}.
     * <p>
     * Different from the method {@link #getDocument}, this method will throw a runtime exception
     * instead of {@code NotFoundException} if the target document is not found.
     *
     * @throws OtherResponseException if an unexpected error occurs when requesting
     * @see #getDocument
     */
    public Document findDocument(RequestWrapper wrapper, SnapshotStrategy<Document> strategy)
        throws OtherResponseException {
        try {
            return getContent(wrapper, defaultHandler, new DocumentHandler(), strategy);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new UnexpectedException(e);
            }
            throw new OtherResponseException(e);
        }
    }

    /**
     * An extension of {@link #getContent} which returns the json content of the response as a Java
     * object and splits the exception if thrown.
     *
     * @throws NotFoundException      if the target object is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     * @see #getObject(RequestWrapper, ObjectMapper, TypeReference, SnapshotStrategy)
     */
    public <T> T getObject(RequestWrapper wrapper, ObjectMapper mapper, Class<? extends T> clazz,
        SnapshotStrategy<T> strategy) throws NotFoundException, OtherResponseException {
        try {
            return getContent(wrapper, defaultHandler, new JsonHandler<>(mapper, clazz), strategy);
        } catch (HttpResponseException e) {
            throw handleException(e);
        }
    }

    /**
     * An extension of {@link #getContent} which returns the json content of the response as a
     * <i>generic</i> Java object and splits the exception if thrown.
     *
     * @throws NotFoundException      if the target object is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     * @see #getObject(RequestWrapper, ObjectMapper, Class, SnapshotStrategy)
     */
    public <T> T getObject(RequestWrapper wrapper, ObjectMapper mapper,
        TypeReference<? extends T> type, SnapshotStrategy<T> strategy)
        throws NotFoundException, OtherResponseException {
        try {
            return getContent(wrapper, defaultHandler, new JsonHandler<>(mapper, type), strategy);
        } catch (HttpResponseException e) {
            throw handleException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (client != null) {
            client.close();
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
}
