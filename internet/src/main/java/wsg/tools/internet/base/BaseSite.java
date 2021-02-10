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
import wsg.tools.internet.base.exception.SiteStatusException;

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
 * Reads and writes caches if there exist caches which are updatable if required.
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

    protected static final double DEFAULT_PERMITS_PER_SECOND = 10D;
    /**
     * temporary directory for cached content.
     */
    private static final String TMPDIR = System.getProperty("java.io.tmpdir") + "tools";
    private static final int TIME_OUT = 30000;

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

    public BaseSite(String name, String domain) {
        this(name, domain, DEFAULT_PERMITS_PER_SECOND);
    }

    public BaseSite(String name, SchemeEnum scheme, String domain) {
        this(name, scheme, domain, DEFAULT_PERMITS_PER_SECOND, DEFAULT_PERMITS_PER_SECOND);
    }

    public BaseSite(String name, String domain, double permitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, permitsPerSecond, permitsPerSecond);
    }

    public BaseSite(String name, SchemeEnum scheme, String domain, double permitsPerSecond, double postPermitsPerSecond) {
        validateStatus(this);
        this.name = name;
        this.scheme = scheme;
        this.domain = domain;
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
     * Validate the status of the site based on the annotation {@link SiteStatus}.
     *
     * @throws SiteStatusException if the status is abnormal
     */
    private void validateStatus(BaseSite site) throws SiteStatusException {
        SiteStatus annotation = site.getClass().getAnnotation(SiteStatus.class);
        if (annotation != null) {
            SiteStatus.Status status = annotation.status();
            if (!SiteStatus.Status.NORMAL.equals(status)) {
                throw new SiteStatusException(annotation);
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
     * Clears all cookies.
     */
    public final void clearCookies() {
        CookieStore store = this.context.getCookieStore();
        if (store != null) {
            store.clear();
        }
        updateContext();
    }

    /**
     * Add cookies manually.
     */
    public final void addCookies(List<? extends Cookie> cookies) {
        CookieStore cookieStore = this.context.getCookieStore();
        if (cookieStore == null) {
            cookieStore = new BasicCookieStore();
        }
        for (Cookie cookie : cookies) {
            cookieStore.addCookie(cookie);
        }
        this.context.setCookieStore(cookieStore);
        updateContext();
    }

    /**
     * Add one cookie manually.
     */
    public final void addCookie(Cookie cookie) {
        CookieStore cookieStore = this.context.getCookieStore();
        if (cookieStore == null) {
            cookieStore = new BasicCookieStore();
        }
        cookieStore.addCookie(cookie);
        this.context.setCookieStore(cookieStore);
        updateContext();
    }

    /**
     * Add cookie manually with the given name-value pair.
     */
    public final void addCookie(String name, String value) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        CookieStore cookieStore = this.context.getCookieStore();
        if (cookieStore != null && cookieStore.getCookies().size() > 0) {
            Cookie cookie0 = cookieStore.getCookies().get(0);
            cookie.setDomain(cookie0.getDomain());
            cookie.setExpiryDate(cookie0.getExpiryDate());
            cookie.setPath(cookie0.getPath());
        } else {
            cookie.setDomain(domain);
            cookie.setExpiryDate(new Date());
            cookie.setPath("/");
        }
        addCookie(cookie);
    }

    /**
     * Return content of get request.
     */
    protected final String getContent(URIBuilder builder, ContentTypeEnum contentType, CacheStrategy strategy) throws NotFoundException {
        return readContent(RequestBuilder.get(builder), contentType, strategy);
    }

    /**
     * Return the document of html content of get request.
     */
    protected final Document getDocument(URIBuilder builder, CacheStrategy strategy) throws NotFoundException {
        String content = readContent(RequestBuilder.get(builder), ContentTypeEnum.HTML, strategy);
        return Jsoup.parse(content);
    }

    /**
     * Return the content of html content of post request.
     */
    protected final Document postDocument(URIBuilder builder, final List<BasicNameValuePair> params, CacheStrategy strategy)
            throws NotFoundException {
        String content = readContent(RequestBuilder.post(builder, params), ContentTypeEnum.HTML, strategy);
        return Jsoup.parse(content);
    }

    /**
     * Return the content of response with a Java object.
     */
    protected final <T> T getObject(URIBuilder builder, Class<T> clazz) throws NotFoundException {
        return getObject(builder, clazz, CacheStrategy.NEVER_UPDATE);
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected final <T> T getObject(URIBuilder builder, TypeReference<T> type) throws NotFoundException {
        return getObject(builder, type, CacheStrategy.NEVER_UPDATE);
    }

    /**
     * Return the content of response with a Java object.
     */
    protected final <T> T getObject(URIBuilder builder, Class<T> clazz, CacheStrategy strategy) throws NotFoundException {
        try {
            String content = readContent(RequestBuilder.get(builder), ContentTypeEnum.JSON, strategy);
            return mapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected final <T> T getObject(URIBuilder builder, TypeReference<T> type, CacheStrategy strategy) throws NotFoundException {
        try {
            String content = readContent(RequestBuilder.get(builder), ContentTypeEnum.JSON, strategy);
            return mapper.readValue(content, type);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the content of post response with a Java object.
     */
    protected final <T> T postObject(URIBuilder builder, List<BasicNameValuePair> params, Class<T> clazz, CacheStrategy strategy)
            throws NotFoundException {
        try {
            String content = readContent(RequestBuilder.post(builder, params), ContentTypeEnum.JSON, strategy);
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
     * @param strategy    strategy of caching
     */
    private String readContent(RequestBuilder builder, ContentTypeEnum contentType, CacheStrategy strategy) throws NotFoundException {
        String filepath = builder.filepath();
        String user = user();
        if (user != null) {
            filepath += "#" + user;
        }
        if (contentType != null) {
            filepath += contentType.getSuffix();
        }
        File file = new File(TMPDIR + filepath);

        String content;
        if (file.isFile()) {
            content = readCache(file);
            if (Constants.NULL_NA.equals(content)) {
                throw new NotFoundException("Not Found");
            }
            if (strategy.ifUpdate(content)) {
                content = updateCache(builder, file);
            }
        } else {
            content = updateCache(builder, file);
        }
        return handleContent(content, contentType);
    }

    private String readCache(File file) {
        log.info("Read from {}", file.getPath());
        try {
            return FileUtils.readFileToString(file, UTF_8);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    private String updateCache(RequestBuilder builder, File file) throws NotFoundException {
        try {
            String content = execute(builder);
            try {
                FileUtils.write(file, content, UTF_8);
            } catch (IOException e) {
                throw AssertUtils.runtimeException(e);
            }
            return content;
        } catch (NotFoundException e) {
            try {
                FileUtils.write(file, Constants.NULL_NA, UTF_8);
            } catch (IOException ex) {
                throw AssertUtils.runtimeException(ex);
            }
            throw e;
        }
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
        String user = this.user();
        String filename = name;
        if (user != null) {
            filename += "#" + user;
        }
        return new File(StringUtils.joinWith(File.separator, TMPDIR, "context", filename + ".cookie"));
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
        return builder("www", path, pathArgs);
    }

    /**
     * Get builder of request uri, including scheme, host, and path.
     */
    protected final URIBuilder builder(String subDomain, String path, Object... pathArgs) {
        URIBuilder builder = new URIBuilder()
                .setScheme(scheme.toString())
                .setHost(StringUtils.isBlank(subDomain) ? domain : subDomain + "." + domain);
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
