package wsg.tools.internet.base.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import wsg.tools.internet.base.CacheResponseWrapper;
import wsg.tools.internet.base.ContentHandler;
import wsg.tools.internet.base.ResponseWrapper;
import wsg.tools.internet.base.SnapshotStrategy;
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
    private final WrappedResponseHandler<String> defaultHandler;
    private final long snapshotLifeCycle;

    protected BaseSite(String name, HttpHost host) {
        this(name, host, defaultResponseHandler());
    }

    protected BaseSite(String name, HttpHost host, WrappedResponseHandler<String> defaultHandler) {
        this(name, host, defaultClient(), defaultContext(), defaultHandler,
            DEFAULT_PERMITS_PER_SECOND, DEFAULT_PERMITS_PER_SECOND, -1);
    }

    protected BaseSite(String name, HttpHost host, CloseableHttpClient client,
        HttpClientContext context, WrappedResponseHandler<String> defaultHandler,
        double permitsPerSecond, double postPermitsPerSecond, long snapshotLifeCycle) {
        SiteUtils.validateStatus(getClass());
        this.name = name;
        this.host = Objects.requireNonNull(host);
        this.limiters = new HashMap<>(2);
        limiters.put(HttpGet.METHOD_NAME, RateLimiter.create(permitsPerSecond));
        limiters.put(HttpPost.METHOD_NAME, RateLimiter.create(postPermitsPerSecond));
        this.client = client;
        this.context = context;
        this.defaultHandler = Objects.requireNonNull(defaultHandler, "defaultHandler");
        this.snapshotLifeCycle = snapshotLifeCycle;
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
    protected static WrappedResponseHandler<String> defaultResponseHandler() {
        return new WrappedStringResponseHandler();
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
     * String}. It generated a string from the response and returns a wrapper containing the headers
     * of the response and an object to which the string content is converted.
     *
     * @param wrapper        the wrapper containing the request to be executed
     * @param contentHandler how to convert the string of the content to an object of target type
     * @param strategy       the strategy of updating the snapshot
     * @return a wrapper of the headers and content of the response
     * @throws HttpResponseException if an error occurs when requesting
     * @see #getContent(RequestWrapper, ContentHandler, SnapshotStrategy)
     */
    public <T> CacheResponseWrapper<T> getResponseWrapper(@Nonnull RequestWrapper wrapper,
        @Nonnull ContentHandler<? extends T> contentHandler, SnapshotStrategy<T> strategy)
        throws HttpResponseException {
        String filepath = wrapper.filepath();
        File cf = new File(TMPDIR + filepath + "." + contentHandler.extension());
        File hf = new File(TMPDIR + filepath + ".header");

        // if the snapshot files do not exist.
        if (!cf.canRead() || !hf.canRead()) {
            return updateSnapshot(wrapper, cf, hf, contentHandler);
        }
        // if the snapshot has expired
        if (snapshotLifeCycle > 0) {
            long expire = cf.lastModified() + snapshotLifeCycle;
            if (System.currentTimeMillis() > expire) {
                return updateSnapshot(wrapper, cf, hf, contentHandler);
            }
        }
        ResponseWrapper<String> rw = readSnapshot(cf, hf);
        T t = contentHandler.handleContent(rw.getContent());
        if (strategy.ifUpdate(t)) {
            return updateSnapshot(wrapper, cf, hf, contentHandler);
        }
        return new CacheableResponseWrapper<>(rw.getHeaders(), t, true);
    }

    private ResponseWrapper<String> readSnapshot(File cf, File hf) {
        ObjectInputStream stream = null;
        try {
            log.info("Read wrapper from {}", cf.getPath());
            String content = FileUtils.readFileToString(cf, Constants.UTF_8);
            stream = new ObjectInputStream(FileUtils.openInputStream(hf));
            return new BasicResponseWrapper<>((Header[]) stream.readObject(), content);
        } catch (IOException | ClassNotFoundException e) {
            throw new UnexpectedException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * An extension of {@link #execute} which assumes that the content of the response is a {@code
     * String}. It generated a string from the response and returns the target object to which the
     * string content is converted.
     *
     * @param wrapper        the wrapper containing the request to be executed
     * @param contentHandler how to convert the string of the content to an object of target type
     * @param strategy       the strategy of updating the snapshot
     * @return the target object to which the string content is converted
     * @throws HttpResponseException if an error occurs when requesting
     * @see #getResponseWrapper(RequestWrapper, ContentHandler, SnapshotStrategy)
     */
    public <T> T getContent(@Nonnull RequestWrapper wrapper,
        @Nonnull ContentHandler<? extends T> contentHandler, SnapshotStrategy<T> strategy)
        throws HttpResponseException {
        String filepath = wrapper.filepath();
        File cf = new File(TMPDIR + filepath + "." + contentHandler.extension());

        // if the snapshot files do not exist.
        if (!cf.canRead()) {
            return updateSnapshot(wrapper, cf, contentHandler);
        }
        // if the snapshot has expired
        if (snapshotLifeCycle > 0) {
            long expire = cf.lastModified() + snapshotLifeCycle;
            if (System.currentTimeMillis() > expire) {
                return updateSnapshot(wrapper, cf, contentHandler);
            }
        }
        String content = null;
        try {
            log.info("Read content from {}", cf.getPath());
            content = FileUtils.readFileToString(cf, Constants.UTF_8);
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
        T t = contentHandler.handleContent(content);
        if (strategy.ifUpdate(t)) {
            return updateSnapshot(wrapper, cf, contentHandler);
        }
        return t;
    }

    private <T> T updateSnapshot(RequestWrapper wrapper, File cf, ContentHandler<? extends T> ch)
        throws HttpResponseException {
        return updateSnapshot(wrapper, cf, null, ch).getContent();
    }

    /**
     * Executes the request, handle the response, return it as an object, and write to the file as a
     * snapshot.
     */
    private <T> CacheResponseWrapper<T> updateSnapshot(RequestWrapper wrapper, File cf,
        File hf, ContentHandler<? extends T> ch)
        throws HttpResponseException {
        HttpHost target = wrapper.getHttpHost();
        if (target == null) {
            target = host;
        }
        ResponseWrapper<String> rw = execute(target, wrapper.build(), defaultHandler);
        try {
            FileUtils.write(cf, rw.getContent(), Constants.UTF_8);
        } catch (IOException e) {
            throw new UnexpectedException(e);
        }
        if (hf != null) {
            try (ObjectOutputStream os = new ObjectOutputStream(FileUtils.openOutputStream(hf))) {
                os.writeObject(rw.getHeaders());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        T t = ch.handleContent(rw.getContent());
        return new CacheableResponseWrapper<>(rw.getHeaders(), t, false);
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
            return getContent(wrapper, new DocumentHandler(), strategy);
        } catch (HttpResponseException e) {
            throw SiteUtils.handleException(e);
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
            return getDocument(wrapper, strategy);
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
     * @see #getObject(RequestWrapper, ObjectMapper, TypeReference, SnapshotStrategy)
     */
    public <T> T getObject(RequestWrapper wrapper, ObjectMapper mapper, Class<? extends T> clazz,
        SnapshotStrategy<T> strategy) throws NotFoundException, OtherResponseException {
        try {
            JsonHandler<? extends T> handler = new JsonHandler<>(mapper, clazz);
            return getContent(wrapper, handler, strategy);
        } catch (HttpResponseException e) {
            throw SiteUtils.handleException(e);
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
            return getContent(wrapper, new JsonHandler<>(mapper, type), strategy);
        } catch (HttpResponseException e) {
            throw SiteUtils.handleException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
    }


}
