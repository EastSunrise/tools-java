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
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
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
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.enums.ContentTypeEnum;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;

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
 * <p>
 * todo concurrency of requests
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
@SuppressWarnings("UnstableApiUsage")
public abstract class BaseSite implements Closeable, ResponseHandler<String> {

    /**
     * Common HTML tags and attributes.
     */
    protected static final String TAG_A = "a";
    protected static final String TAG_INPUT = "input";
    protected static final String TAG_FIELDSET = "fieldset";
    protected static final String TAG_LI = "li";
    protected static final String TAG_OPTION = "option";
    protected static final String TAG_SPAN = "span";
    protected static final String TAG_STRONG = "strong";
    protected static final String TAG_H3 = "h3";
    protected static final String TAG_H4 = "h4";
    protected static final String TAG_TIME = "time";
    protected static final String TAG_FONT = "font";
    protected static final String TAG_SCRIPT = "script";
    protected static final String ATTR_HREF = "href";
    protected static final String ATTR_NAME = "name";
    protected static final String ATTR_SRC = "src";
    protected static final String ATTR_DATETIME = "datetime";
    protected static final String ATTR_CONTENT = "content";
    /**
     * temporary directory for cached content.
     */
    private static final String TMPDIR = System.getProperty("java.io.tmpdir") + "tools";
    private static final int TIME_OUT = 30000;

    protected final ObjectMapper mapper;
    @Getter
    private final String name;
    private final SchemeEnum scheme;
    private final String host;
    private final HttpClientContext context;
    private final Map<String, RateLimiter> limiters;
    private final CloseableHttpClient client;

    public BaseSite(String name, String host) {
        this(name, host, 10D);
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
        this.client = HttpClientBuilder.create()
                .setDefaultHeaders(Collections.singletonList(
                        new BasicHeader(HTTP.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                "(KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36")))
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
        this.context = HttpClientContext.create();
        this.context.setRequestConfig(RequestConfig.custom()
                .setConnectTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).build());
        File file = cookieFile();
        if (file.canRead()) {
            try (ObjectInputStream stream = new ObjectInputStream(FileUtils.openInputStream(file))) {
                log.info("Read cached cookies: {}.", file.getPath());
                CookieStore cookieStore = (CookieStore) stream.readObject();
                this.context.setCookieStore(cookieStore);
            } catch (IOException | ClassNotFoundException e) {
                throw AssertUtils.runtimeException(e);
            }
        }
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
     * Clear current cookies to log out.
     */
    public final void logout() {
        if (user() != null) {
            this.context.setCookieStore(null);
            updateContext();
        }
    }

    /**
     * Add cookie manually.
     */
    public final void addCookie(String name, String value) {
        CookieStore cookieStore = this.context.getCookieStore();
        if (cookieStore == null) {
            cookieStore = new BasicCookieStore();
        }
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        String domain = host;
        String path = "/";
        Date expiredDate = new Date();
        if (cookieStore.getCookies().size() > 0) {
            Cookie cookie0 = cookieStore.getCookies().get(0);
            path = cookie0.getPath();
            expiredDate = cookie0.getExpiryDate();
            domain = cookie0.getDomain();
        }
        cookie.setDomain(domain);
        cookie.setExpiryDate(expiredDate);
        cookie.setPath(path);
        cookieStore.addCookie(cookie);
        this.context.setCookieStore(cookieStore);
    }

    /**
     * Return content of get request.
     */
    protected final String getContent(URIBuilder builder, ContentTypeEnum contentType, boolean cached) throws NotFoundException {
        return readContent(RequestBuilder.get(builder), contentType, cached);
    }

    /**
     * Return the document of html content of get request.
     */
    protected final Document getDocument(URIBuilder builder, boolean cached) throws NotFoundException {
        String content = readContent(RequestBuilder.get(builder), ContentTypeEnum.HTML, cached);
        return Jsoup.parse(content);
    }

    /**
     * Return the content of html content of post request.
     */
    protected final Document postDocument(URIBuilder builder, final List<BasicNameValuePair> params, boolean cached)
            throws NotFoundException {
        String content = readContent(RequestBuilder.post(builder, params), ContentTypeEnum.HTML, cached);
        return Jsoup.parse(content);
    }

    /**
     * Return the content of response with a Java object.
     */
    protected final <T> T getObject(URIBuilder builder, Class<T> clazz) throws NotFoundException {
        return getObject(builder, clazz, true);
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected final <T> T getObject(URIBuilder builder, TypeReference<T> type) throws NotFoundException {
        return getObject(builder, type, true);
    }

    /**
     * Return the content of response with a Java object.
     */
    protected final <T> T getObject(URIBuilder builder, Class<T> clazz, boolean cached) throws NotFoundException {
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
    protected final <T> T getObject(URIBuilder builder, TypeReference<T> type, boolean cached) throws NotFoundException {
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
    protected final <T> T postObject(URIBuilder builder, List<BasicNameValuePair> params, Class<T> clazz, boolean cached)
            throws NotFoundException {
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
     */
    private String readContent(RequestBuilder builder, ContentTypeEnum contentType, boolean cached) throws NotFoundException {
        String content;
        if (!cached) {
            content = execute(builder);
        } else {
            String filepath = builder.filepath();
            String user = user();
            if (user != null) {
                filepath += "#" + user;
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
                    throw new NotFoundException("Not Found");
                }
            } else {
                try {
                    content = execute(builder);
                    try {
                        FileUtils.write(file, content, UTF_8);
                    } catch (IOException e) {
                        throw AssertUtils.runtimeException(e);
                    }
                } catch (NotFoundException e) {
                    try {
                        FileUtils.write(file, Constants.NULL_NA, UTF_8);
                    } catch (IOException ex) {
                        throw AssertUtils.runtimeException(ex);
                    }
                    throw e;
                }
            }
        }
        return handleContent(content, contentType);
    }

    private String execute(RequestBuilder builder) throws NotFoundException {
        handleRequest(builder, context);
        log.info("{} from {}", builder.getMethod(), builder.displayUrl());
        limiters.get(builder.getMethod()).acquire();
        try {
            return client.execute(builder.build(), this, context);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException(e.getReasonPhrase());
            }
            throw AssertUtils.runtimeException(e);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
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

    @Override
    public String handleResponse(HttpResponse response) throws IOException {
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        return entity == null ? null : handleEntity(entity);
    }

    /**
     * Handle the response entity and transform it into string.
     */
    protected String handleEntity(HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity, Constants.UTF_8);
    }

    /**
     * Handle content before returning as an object or a document.
     *
     * @param content     source content
     * @param contentType type of the content
     * @return handled content
     */
    protected String handleContent(String content, ContentTypeEnum contentType) throws NotFoundException {
        return content;
    }

    /**
     * Update context of this site.
     * It should be called when logging in the site or the site is going to be closed, as {@link #close()} is called.
     */
    protected final void updateContext() {
        try (ObjectOutputStream stream = new ObjectOutputStream(FileUtils.openOutputStream(cookieFile()))) {
            log.info("Synchronize cookies of {}.", getName());
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
