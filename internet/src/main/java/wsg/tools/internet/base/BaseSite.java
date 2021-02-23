package wsg.tools.internet.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.enums.SchemeEnum;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Base class for a website.
 * <p>
 * Its core method is {@link #execute(RequestBuilder, ResponseHandler)} which executes the given request, handles the response
 * by the given handler and returns the response as an object of target type: request → response → {@code <T>}.
 * Methods {@link #handleRequest(RequestBuilder, HttpContext)} is overrideable to handle request before executing.
 * <p>
 * Method {@link #getContent(RequestBuilder, ContentHandler, SnapshotStrategy)} is an extension of {@link #execute}. It executes
 * the given request, handles the response as a String, handles the String by the given {@code ContentHandler}, and finally
 * returns the Sting as an object of target type: request → response → String → {@code T}.
 * Methods {@link #handleResponse(HttpResponse)} and {@link #handleEntity(HttpEntity)} are overrideable to handle the response
 * of the request before returning the content.
 * <p>
 * Methods {@link #getObject} are to obtain the json content and return as a given Java object.
 * Methods {@link #getDocument} and {@link #postDocument} are to obtain the html content and return as a {@link Document}.
 * <p>
 * todo concurrency of requests
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
@SuppressWarnings("UnstableApiUsage")
public abstract class BaseSite implements Closeable {

    protected static final double DEFAULT_PERMITS_PER_SECOND = 10D;
    /**
     * temporary directory for snapshots and cookies.
     */
    private static final String TMPDIR = System.getProperty("java.io.tmpdir") + "tools";
    private static final Header USER_AGENT = new BasicHeader(HTTP.USER_AGENT,
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
    private static final int TIME_OUT = 30000;
    private static final int MIN_ERROR_STATUS_CODE = 300;

    protected final ObjectMapper mapper;
    @Getter
    private final String name;
    @Getter
    private final SchemeEnum scheme;
    @Getter
    private final String domain;
    private final HttpClientContext context;
    private final Map<String, RateLimiter> limiters;
    private final CloseableHttpClient client;
    private final ExecutorService executor =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory());

    public BaseSite(String name, String domain) {
        this(name, domain, DEFAULT_PERMITS_PER_SECOND);
    }

    public BaseSite(String name, String domain, double permitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, permitsPerSecond, permitsPerSecond);
    }

    public BaseSite(String name, SchemeEnum scheme, String domain) {
        this(name, scheme, domain, DEFAULT_PERMITS_PER_SECOND, DEFAULT_PERMITS_PER_SECOND);
    }

    public BaseSite(String name, SchemeEnum scheme, String domain, double permitsPerSecond, double postPermitsPerSecond) {
        SiteStatus.Status.validateStatus(this.getClass());
        this.name = name;
        this.scheme = scheme;
        this.domain = domain;
        this.mapper = objectMapper();
        this.limiters = new HashMap<>(2);
        this.limiters.put(HttpGet.METHOD_NAME, RateLimiter.create(permitsPerSecond));
        this.limiters.put(HttpPost.METHOD_NAME, RateLimiter.create(postPermitsPerSecond));
        this.client = HttpClientBuilder.create().setDefaultHeaders(Collections.singletonList(USER_AGENT))
                .setConnectionManager(new PoolingHttpClientConnectionManager()).build();
        this.context = HttpClientContext.create();
        this.context.setRequestConfig(RequestConfig.custom().setConnectTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).build());
        File file = cookieFile();
        if (file.canRead()) {
            try (ObjectInputStream stream = new ObjectInputStream(FileUtils.openInputStream(file))) {
                log.info("Read cookies from {}.", file.getPath());
                CookieStore cookieStore = (CookieStore) stream.readObject();
                this.context.setCookieStore(cookieStore);
            } catch (IOException | ClassNotFoundException e) {
                throw AssertUtils.runtimeException(e);
            }
        }
    }

    @Override
    public final void close() throws IOException {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
        if (client != null) {
            client.close();
        }
    }

    /**
     * Return the html content of the response as a {code Document}.
     */
    protected final Document getDocument(URIBuilder builder, SnapshotStrategy strategy) throws HttpResponseException {
        return getContent(RequestBuilder.get(builder), ContentHandlers.DOCUMENT_CONTENT_HANDLER, strategy);
    }

    /**
     * Return the html content of the response as a {code Document}.
     */
    protected final Document postDocument(URIBuilder builder, final List<BasicNameValuePair> params, SnapshotStrategy strategy) throws HttpResponseException {
        return getContent(RequestBuilder.post(builder, params), ContentHandlers.DOCUMENT_CONTENT_HANDLER, strategy);
    }

    /**
     * Return the json content of the response as a Java object.
     */
    protected final <T> T getObject(URIBuilder builder, Class<T> clazz) throws HttpResponseException {
        return getObject(builder, clazz, SnapshotStrategy.NEVER_UPDATE);
    }

    /**
     * Return the json content of the response as a Java object.
     */
    protected final <T> T getObject(URIBuilder builder, Class<T> clazz, SnapshotStrategy strategy) throws HttpResponseException {
        return getContent(RequestBuilder.get(builder), ContentHandlers.getJsonHandler(mapper, clazz), strategy);
    }

    /**
     * Return the json content of the response as a generic Java object.
     */
    protected final <T> T getObject(URIBuilder builder, TypeReference<T> type) throws HttpResponseException {
        return getObject(builder, type, SnapshotStrategy.NEVER_UPDATE);
    }

    /**
     * Return the json content of the response as a generic Java object.
     */
    protected final <T> T getObject(URIBuilder builder, TypeReference<T> type, SnapshotStrategy strategy) throws HttpResponseException {
        return getContent(RequestBuilder.get(builder), ContentHandlers.getJsonHandler(mapper, type), strategy);
    }

    /**
     * Obtains the content of the response of the request and returns it as an object of type {@code T} by the given handler.
     *
     * @param handler  how to handle the content and return as an object of type {@code T}
     * @param strategy the strategy of updating the snapshot
     */
    protected final <T> T getContent(RequestBuilder builder, ContentHandler<T> handler, SnapshotStrategy strategy) throws HttpResponseException {
        String filepath = builder.filepath();
        if (this instanceof Loggable) {
            Object user = ((Loggable<?>) this).user();
            if (user != null) {
                filepath += "#" + user;
            }
        }
        filepath += Constants.FILE_EXTENSION_SEPARATOR + handler.suffix();
        File file = new File(TMPDIR + filepath);

        String content;
        if (file.isFile()) {
            log.info("Read from {}", file.getPath());
            try {
                content = FileUtils.readFileToString(file, UTF_8);
            } catch (IOException e) {
                throw AssertUtils.runtimeException(e);
            }
            if (strategy.ifUpdate(content)) {
                content = updateSnapshot(builder, file);
            }
        } else {
            content = updateSnapshot(builder, file);
        }
        return handler.handleContent(content);
    }

    /**
     * Obtains uri builder of main host.
     */
    protected final URIBuilder builder0(String path, Object... pathArgs) {
        return builder("www", path, pathArgs);
    }

    /**
     * Obtains builder of request uri, including scheme, host, and path.
     */
    protected final URIBuilder builder(String subDomain, String path, Object... pathArgs) {
        URIBuilder builder = new URIBuilder().setScheme(scheme.toString())
                .setHost(StringUtils.isBlank(subDomain) ? domain : subDomain + "." + domain);
        if (StringUtils.isNotBlank(path)) {
            builder.setPath(String.format(path, pathArgs));
        }
        return builder;
    }

    /**
     * Obtains cookie of the given name, may null.
     */
    @Nullable
    protected final Cookie getCookie(String name) {
        CookieStore cookieStore = this.context.getCookieStore();
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

    /**
     * Pre-handle the request before the request is sent to the server.
     *
     * @param builder the request to preprocess
     * @param context the context for the request
     */
    protected void handleRequest(RequestBuilder builder, HttpContext context) {
    }

    /**
     * Handle response of the request.
     *
     * @param response response to handle
     * @return target content of the response
     * @throws HttpResponseException wrong response
     * @throws IOException           if an error occurs when reading the stream
     */
    protected String handleResponse(HttpResponse response) throws IOException {
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= MIN_ERROR_STATUS_CODE) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
        return entity == null ? null : handleEntity(entity);
    }

    /**
     * Handle the response entity and transform it into string.
     *
     * @param entity entity to handle
     * @return target content in the entity
     * @throws IOException if an error occurs when reading the stream
     */
    protected String handleEntity(HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity, Constants.UTF_8);
    }

    /**
     * Initialize {@link ObjectMapper}
     * <p>
     * Override it to customize Jackson if necessary.
     */
    protected ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    private File cookieFile() {
        String filepath = RequestBuilder.get(new URIBuilder().setScheme(scheme.toString()).setHost(domain)).filepath();
        return new File(StringUtils.joinWith(File.separator, TMPDIR, "context", filepath + ".cookie"));
    }

    /**
     * Executes the request, handle the response, return it as a String, and write to the file as a snapshot.
     */
    private String updateSnapshot(RequestBuilder builder, File file) throws HttpResponseException {
        String content = execute(builder, this::handleResponse);
        try {
            FileUtils.write(file, content, UTF_8);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
        return content;
    }

    /**
     * Executes the request and handle the response by the given handler.
     *
     * @return entity from the response
     */
    protected final <T> T execute(RequestBuilder builder, ResponseHandler<T> handler) throws HttpResponseException {
        handleRequest(builder, context);
        log.info("{} from {}", builder.getMethod(), builder.displayUrl());
        limiters.get(builder.getMethod()).acquire();
        try {
            T entity = client.execute(builder.build(), handler, context);
            executor.execute(() -> {
                try (ObjectOutputStream stream = new ObjectOutputStream(FileUtils.openOutputStream(cookieFile()))) {
                    log.info("Synchronize cookies of {}.", getDomain());
                    stream.writeObject(context.getCookieStore());
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            });
            return entity;
        } catch (HttpResponseException e) {
            throw e;
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
    }
}
