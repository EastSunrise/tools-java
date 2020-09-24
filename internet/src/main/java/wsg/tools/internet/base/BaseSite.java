package wsg.tools.internet.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.util.AssertUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Base class for a website.
 * <p>
 * Steps to complete a request are as follows:
 * <ul>
 *      <li>Construct a {@link HttpRequestBase}
 *          which is based on scheme, host of the site and given arguments like path, query parameters.</li>
 *      <li>Read caches if cached.</li>
 *      <li>Execute the request and obtain content of the response.</li>
 *      <li>Cache the content to temporary directory if required.</li>
 *      <li>Transfer obtained content to target format, such as Java object, {@link Document}</li>
 * </ul>
 * Step 1 and 5 are completed in the following methods:
 * <ul>
 *     <li>{@link #getDocument}</li>
 *     <li>{@link #postDocument}</li>
 * </ul>
 * which handle get, post, or webdriver requests and return {@link Document}.
 * And:
 * <ul>
 *     <li>{@link #getObject}</li>
 * </ul>
 * which handle json requests and return Java objects.
 * <p>
 * Method {@link #readContent} is main method to obtains target content.
 * Reads and writes caches if caches are used.
 * Otherwise call execution methods to do the request.
 * Overrideable methods are invoked to handle request and response in the process.
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
@SuppressWarnings("UnstableApiUsage")
public abstract class BaseSite implements Closeable {

    protected static final String TAG_A = "a";
    protected static final String TAG_LI = "li";
    protected static final String TAG_SMALL = "small";
    protected static final String TAG_STRONG = "strong";
    protected static final String TAG_TR = "tr";
    protected static final String TAG_H3 = "h3";
    protected static final String TAG_TIME = "time";
    protected static final String ATTR_HREF = "href";
    protected static final String ATTR_TITLE = "title";
    protected static final String ATTR_DATETIME = "datetime";

    protected static final int CONNECT_TIME_OUT = 30000;
    protected static final int SOCKET_TIME_OUT = 30000;

    private static final double DEFAULT_PERMIT_PER_SECOND = 10D;
    private static final String TMPDIR = System.getProperty("java.io.tmpdir") + "tools";

    @Getter
    protected final String name;
    protected final SchemeEnum scheme;
    protected final String host;
    protected final ObjectMapper mapper;
    private final ResponseHandler<String> responseHandler;
    private final HttpClientContext context;
    private final Map<String, RateLimiter> limiters;
    private final CloseableHttpClient client;

    public BaseSite(String name, String host) {
        this(name, host, DEFAULT_PERMIT_PER_SECOND);
    }

    public BaseSite(String name, String host, double permitsPerSecond) {
        this(name, SchemeEnum.HTTPS, host, permitsPerSecond, permitsPerSecond);
    }

    public BaseSite(String name, SchemeEnum scheme, String host, double permitsPerSecond, double postPermitsPerSecond) {
        this.name = name;
        this.scheme = scheme;
        this.host = host;
        this.mapper = objectMapper();
        this.limiters = new HashMap<>(2);
        this.limiters.put(HttpGet.METHOD_NAME, RateLimiter.create(permitsPerSecond));
        this.limiters.put(HttpPost.METHOD_NAME, RateLimiter.create(postPermitsPerSecond));
        this.context = initContext();
        this.client = HttpClientBuilder.create()
                .setDefaultHeaders(Collections.singletonList(
                        new BasicHeader(HTTP.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                "(KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36")))
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
        this.responseHandler = new BasicResponseHandler() {
            @Override
            public String handleEntity(HttpEntity entity) throws IOException {
                return EntityUtils.toString(entity, Constants.UTF_8);
            }
        };
    }

    /**
     * Obtains current user, null if not logon yet.
     *
     * @return object of user, may identity or username
     */
    @Nullable
    public String user() {
        return null;
    }

    /**
     * Return the document of html content of get request.
     */
    protected final Document getDocument(URIBuilder builder, boolean cached) throws IOException {
        try {
            String content = readContent(RequestBuilder.get(builder), ContentTypeEnum.HTML, cached);
            return Jsoup.parse(content);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the content of html content of post request.
     */
    protected final Document postDocument(URIBuilder builder, final List<BasicNameValuePair> params, boolean cached) throws IOException {
        try {
            String content = readContent(RequestBuilder.post(builder, params), ContentTypeEnum.HTML, cached);
            return Jsoup.parse(content);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the content of response with a Java object.
     */
    protected final <T> T getObject(URIBuilder builder, Class<T> clazz) throws IOException {
        return getObject(builder, clazz, true);
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected final <T> T getObject(URIBuilder builder, TypeReference<T> type) throws IOException {
        return getObject(builder, type, true);
    }

    /**
     * Return the content of response with a Java object.
     */
    protected final <T> T getObject(URIBuilder builder, Class<T> clazz, boolean cached) throws IOException {
        try {
            String content = readContent(RequestBuilder.get(builder), ContentTypeEnum.JSON, cached);
            return mapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected final <T> T getObject(URIBuilder builder, TypeReference<T> type, boolean cached) throws IOException {
        try {
            String content = readContent(RequestBuilder.get(builder), ContentTypeEnum.JSON, cached);
            return mapper.readValue(content, type);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the content of post response with a Java object.
     */
    protected final <T> T postObject(URIBuilder builder, List<BasicNameValuePair> params, Class<T> clazz, boolean cached) throws IOException {
        try {
            String content = readContent(RequestBuilder.post(builder, params), ContentTypeEnum.JSON, cached);
            return mapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Obtains content.
     *
     * @param builder     builder for request.
     * @param contentType content type of the response
     * @param cached      whether to read cached content
     * @throws IOException only thrown when not loaded
     */
    private String readContent(RequestBuilder builder, ContentTypeEnum contentType, boolean cached) throws IOException {
        String content;
        if (!cached) {
            content = execute(builder);
        } else {
            String filepath = builder.filepath();
            String user = user();
            if (user != null) {
                filepath += SignEnum.HASH.toString() + user;
            }
            if (contentType != null) {
                filepath += contentType.getSuffix();
            }
            File file = new File(TMPDIR + filepath);

            if (file.isFile()) {
                log.info("Read from {}", file.getPath());
                try {
                    content = FileUtils.readFileToString(file, UTF_8);
                } catch (IOException e) {
                    throw AssertUtils.runtimeException(e);
                }
                if (Constants.NULL_NA.equals(content)) {
                    throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Not Found");
                }
            } else {
                try {
                    content = execute(builder);
                    try {
                        FileUtils.write(file, content, UTF_8);
                    } catch (IOException e) {
                        throw AssertUtils.runtimeException(e);
                    }
                } catch (HttpResponseException e) {
                    if (HttpStatus.SC_NOT_FOUND == e.getStatusCode()) {
                        try {
                            FileUtils.write(file, Constants.NULL_NA, UTF_8);
                        } catch (IOException ex) {
                            throw AssertUtils.runtimeException(ex);
                        }
                    }
                    throw e;
                }
            }
        }
        return handleContent(content, contentType);
    }

    private String execute(RequestBuilder builder)
            throws IOException {
        handleRequest(builder, context);
        log.info("{} from {}", builder.getMethod(), builder.displayUrl());
        log.info("Slept for {}s.", limiters.get(builder.getMethod()).acquire());
        return client.execute(builder.build(), responseHandler, context);
    }

    /**
     * Pre-handle the uri.
     * On the client side, this step is performed before the request is
     * sent to the server. On the server side, this step is performed
     * on incoming messages before the message body is evaluated.
     *
     * @param builder the request to preprocess
     * @param context the context for the request
     */
    protected void handleRequest(RequestBuilder builder, HttpContext context) { }

    /**
     * Handle content before returning as an object or a document.
     *
     * @param content     source content
     * @param contentType type of the content
     * @return handled content
     * @throws HttpResponseException if the content contains an error
     */
    protected String handleContent(String content, ContentTypeEnum contentType) throws HttpResponseException {
        return content;
    }

    /**
     * Initialize context of the site. Read cached cookies if any.
     */
    private HttpClientContext initContext() {
        File file = cookieFile();
        HttpClientContext context = HttpClientContext.create();
        context.setRequestConfig(RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIME_OUT).setSocketTimeout(SOCKET_TIME_OUT).build());
        if (!file.isFile()) {
            return context;
        }
        try (ObjectInputStream stream = new ObjectInputStream(FileUtils.openInputStream(cookieFile()))) {
            log.info("Read cached cookies: {}.", file.getPath());
            CookieStore cookieStore = (CookieStore) stream.readObject();
            context.setCookieStore(cookieStore);
            return context;
        } catch (IOException | ClassNotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Update context of this site.
     * It should be called when logging in the site or the site is going to be closed, as {@link #close()} is called.
     */
    protected final void updateContext() {
        try (ObjectOutputStream stream = new ObjectOutputStream(FileUtils.openOutputStream(cookieFile()))) {
            log.info("Synchronize cookies.");
            stream.writeObject(this.context.getCookieStore());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private File cookieFile() {
        return new File(StringUtils.joinWith(File.separator, TMPDIR, "context", name + ".cookie"));
    }

    /**
     * Obtains current cookies.
     */
    @Nonnull
    protected final List<Cookie> getCookies() {
        CookieStore cookieStore = this.context.getCookieStore();
        if (cookieStore == null) {
            return new ArrayList<>();
        }
        return cookieStore.getCookies();
    }

    protected final URIBuilder builder0(String path, Object... pathArgs) {
        return builder(null, path, pathArgs);
    }

    /**
     * Get builder of request uri, including scheme, host, and path.
     */
    protected final URIBuilder builder(String subHost, String path, Object... pathArgs) {
        URIBuilder builder = new URIBuilder()
                .setScheme(scheme.toString())
                .setHost(StringUtils.isBlank(subHost) ? host : subHost + "." + host);
        if (StringUtils.isNotBlank(path)) {
            builder.setPath(String.format(path, pathArgs));
        }
        return builder;
    }

    /**
     * Initialize {@link ObjectMapper}
     * <p>
     * Override it to customize Jackson if necessary.
     */
    protected ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Override
    public final void close() throws IOException {
        updateContext();
        if (client != null) {
            client.close();
        }
    }
}
