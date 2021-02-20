package wsg.tools.internet.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Base class for a website.
 * <p>
 * Steps to complete a request are as follows:
 * <ul>
 *      <li>Construct a {@link HttpRequestBase} which is based on scheme, host of the site and given arguments like path, query parameters.</li>
 *      <li>Execute the request to obtain target content which can be stored as a snapshot in case of next access.</li>
 *      <li>Transfer obtained content to target format, such as Java object, {@link Document}, or else.</li>
 * </ul>
 * Following methods are called to construct a request and transfer format of the content.
 * <ul>
 *     <li>{@link #getDocument}</li>
 *     <li>{@link #postDocument}</li>
 *     <li>{@link #getObject}</li>
 * </ul>
 * <p>
 * Method {@link #request} is main method to handle the snapshots which will be updated based on the {@code SnapshotStrategy}.
 * Method {@link #execute(RequestBuilder)} is called to execute a real request for target content.
 * <p>
 * Following methods is overrideable to handle request or response in the process:
 * <ul>
 *     <li>{@link #handleEntity(HttpEntity)}</li>
 *     <li>{@link #handleRequest(RequestBuilder, HttpContext)}</li>
 *     <li>{@link #handleResponse(HttpResponse)}</li>
 *     <li>{@link ContentHandler#handleContent(String)}</li>
 * </ul>
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
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
        SiteStatus.Status.validateStatus(this);
        this.name = name;
        this.scheme = scheme;
        this.domain = domain;
        this.mapper = objectMapper();
        this.limiters = new HashMap<>(2);
        this.limiters.put(HttpGet.METHOD_NAME, RateLimiter.create(permitsPerSecond));
        this.limiters.put(HttpPost.METHOD_NAME, RateLimiter.create(postPermitsPerSecond));
        this.client =
                HttpClientBuilder.create().setDefaultHeaders(Collections.singletonList(new BasicHeader(HTTP.USER_AGENT,
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                                + "(KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"))).setConnectionManager(new PoolingHttpClientConnectionManager()).build();
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
     * Return the document of html content of get request.
     */
    protected final Document getDocument(URIBuilder builder, SnapshotStrategy strategy) throws HttpResponseException {
        return request(RequestBuilder.get(builder), ContentHandlers.DOCUMENT_CONTENT_HANDLER, strategy);
    }

    /**
     * Return the content of html content of post request.
     */
    protected final Document postDocument(URIBuilder builder, final List<BasicNameValuePair> params, SnapshotStrategy strategy) throws HttpResponseException {
        return request(RequestBuilder.post(builder, params), ContentHandlers.DOCUMENT_CONTENT_HANDLER, strategy);
    }

    /**
     * Return the content of response with a Java object.
     */
    protected final <T> T getObject(URIBuilder builder, Class<T> clazz) throws HttpResponseException {
        return getObject(builder, clazz, SnapshotStrategy.NEVER_UPDATE);
    }

    /**
     * Return the content of response with a Java object.
     */
    protected final <T> T getObject(URIBuilder builder, Class<T> clazz, SnapshotStrategy strategy) throws HttpResponseException {
        return request(RequestBuilder.get(builder), ContentHandlers.getJsonHandler(mapper, clazz), strategy);
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected final <T> T getObject(URIBuilder builder, TypeReference<T> type) throws HttpResponseException {
        return getObject(builder, type, SnapshotStrategy.NEVER_UPDATE);
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected final <T> T getObject(URIBuilder builder, TypeReference<T> type, SnapshotStrategy strategy) throws HttpResponseException {
        return request(RequestBuilder.get(builder), ContentHandlers.getJsonHandler(mapper, type), strategy);
    }

    /**
     * Obtains target object by executing the request whose response can be written to a snapshot.
     */
    protected final <T> T request(RequestBuilder builder, ContentHandler<T> handler, SnapshotStrategy strategy) throws HttpResponseException {
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
        URIBuilder builder = new URIBuilder().setScheme(scheme.toString()).setHost(StringUtils.isBlank(subDomain) ?
                domain :
                subDomain + "." + domain);
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

    private String updateSnapshot(RequestBuilder builder, File file) throws HttpResponseException {
        String content = execute(builder);
        try {
            FileUtils.write(file, content, UTF_8);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
        return content;
    }

    private String execute(RequestBuilder builder) throws HttpResponseException {
        handleRequest(builder, context);
        log.info("{} from {}", builder.getMethod(), builder.displayUrl());
        limiters.get(builder.getMethod()).acquire();
        try {
            String content = client.execute(builder.build(), this::handleResponse, context);
            executor.execute(() -> {
                try (ObjectOutputStream stream = new ObjectOutputStream(FileUtils.openOutputStream(cookieFile()))) {
                    log.info("Synchronize cookies of {}.", getName());
                    stream.writeObject(context.getCookieStore());
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            });
            return content;
        } catch (HttpResponseException e) {
            throw e;
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
    }
}
