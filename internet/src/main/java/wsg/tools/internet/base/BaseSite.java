package wsg.tools.internet.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.function.throwable.ThrowableFunction;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.StringUtilsExt;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
 *     <li>{@link #loadDocument}</li>
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
 * There are also three methods set to execute different request:
 * <ul>
 *     <li>{@link #getContent} for "Get"</li>
 *     <li>{@link #postContent} for "Post"</li>
 *     <Li>{@link #loadContent} to load content from webdriver</Li>
 * </ul>
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
@SuppressWarnings("UnstableApiUsage")
public abstract class BaseSite<U> implements Closeable, Loggable<U> {

    protected static final String TAG_A = "a";
    protected static final String TAG_LI = "li";
    protected static final String TAG_SMALL = "small";
    protected static final String TAG_STRONG = "strong";
    protected static final String TAG_TR = "tr";
    protected static final String TAG_H3 = "h3";
    protected static final String TAG_DL = "dl";
    protected static final String ATTR_HREF = "href";

    protected static final int CONNECT_TIME_OUT = 30000;
    protected static final int SOCKET_TIME_OUT = 30000;

    private static final double DEFAULT_PERMIT_PER_SECOND = 10D;
    private static final Collection<? extends Header> DEFAULT_HEADERS = Collections.singletonList(
            new BasicHeader(HTTP.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"));
    private static final RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(CONNECT_TIME_OUT).setSocketTimeout(SOCKET_TIME_OUT).build();
    private static final ResponseHandler<String> DEFAULT_RESPONSE_HANDLER = new BasicResponseHandler() {
        @Override
        public String handleEntity(HttpEntity entity) throws IOException {
            return EntityUtils.toString(entity, Constants.UTF_8);
        }
    };
    private static final String TMPDIR = System.getProperty("java.io.tmpdir") + "tools";

    @Getter
    protected final String name;
    protected final SchemeEnum scheme;
    protected final String host;
    protected final ObjectMapper mapper;
    private final HttpClientContext context;
    private final RateLimiter limiter;
    private final RateLimiter postLimiter;
    private final CloseableHttpClient client;
    private WebDriver webDriver;

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
        this.limiter = RateLimiter.create(permitsPerSecond);
        this.postLimiter = RateLimiter.create(postPermitsPerSecond);
        this.context = readContext();
        this.client = HttpClientBuilder.create()
                .setDefaultHeaders(DEFAULT_HEADERS)
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
    }

    @Override
    public boolean login(String username, String password) throws IOException {
        return false;
    }

    @Override
    public U user() {
        return null;
    }

    /**
     * Return the document of html content of get request.
     */
    protected final Document getDocument(URIBuilder builder, boolean cached) throws IOException {
        try {
            String content = readContent(builder, this::getContent, ContentTypeEnum.HTML, cached, null);
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
            String content = readContent(builder, b -> this.postContent(b, params), ContentTypeEnum.HTML, cached, params);
            return Jsoup.parse(content);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the document of html content loaded by webdriver.
     */
    protected final Document loadDocument(URIBuilder builder, boolean cached) throws IOException {
        try {
            String content = readContent(builder, this::loadContent, ContentTypeEnum.HTML, cached, null);
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
            String content = readContent(builder, this::getContent, ContentTypeEnum.JSON, cached, null);
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
            String content = readContent(builder, this::getContent, ContentTypeEnum.JSON, cached, null);
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
            String content = readContent(builder, b -> this.postContent(b, params), ContentTypeEnum.JSON, cached, params);
            return mapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Obtains content.
     *
     * @param builder     specified uri
     * @param execution   function to execute to obtain content by the given uri.
     * @param contentType content type of the response
     * @param cached      whether to read cached content
     * @param params      params for post request, otherwise null
     * @throws IOException only thrown when not loaded
     */
    private String readContent(
            URIBuilder builder, ThrowableFunction<URIBuilder, String, IOException> execution,
            ContentTypeEnum contentType, boolean cached, List<BasicNameValuePair> params) throws IOException {
        String content;
        if (!cached) {
            content = execute(builder, execution, contentType);
        } else {
            File file = cachedFile(builder, contentType, params);
            if (file.isFile()) {
                log.info("Read from {}", file.getPath());
                try {
                    content = FileUtils.readFileToString(file, UTF_8);
                    if (Constants.NULL_NA.equals(content)) {
                        throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Not Found");
                    }
                } catch (IOException e) {
                    throw AssertUtils.runtimeException(e);
                }
            } else {
                try {
                    content = execute(builder, execution, contentType);
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

    /**
     * Build path of cached file by uri and type of content.
     */
    private File cachedFile(URIBuilder uriBuilder, ContentTypeEnum contentType, List<BasicNameValuePair> params) {
        StringBuilder builder = new StringBuilder();

        if (uriBuilder.isAbsolute()) {
            builder.append(File.separator).append(scheme);
        }

        String host = uriBuilder.getHost();
        String[] parts = host.split("\\.");
        for (int i = parts.length - 1; i >= 0; i--) {
            builder.append(File.separator).append(parts[i]);
        }

        if (!uriBuilder.isPathEmpty()) {
            for (String part : uriBuilder.getPathSegments()) {
                if (!"".equals(part)) {
                    builder.append(File.separator).append(part);
                }
            }
        }

        if (!uriBuilder.isQueryEmpty()) {
            builder.append(File.separator).append(URLEncodedUtils.format(uriBuilder.getQueryParams(), Consts.UTF_8));
        }

        if (params != null) {
            builder.append(File.separator).append(StringUtils.join(params, SignEnum.AND.getC()));
        }

        U user = user();
        if (user != null) {
            builder.append(SignEnum.HASH).append(user);
        }

        if (contentType != null) {
            builder.append(contentType.getSuffix());
        }

        return new File(TMPDIR + StringUtilsExt.toFilename(builder.toString()));
    }

    private String execute(
            URIBuilder builder, ThrowableFunction<URIBuilder, String, IOException> execution, ContentTypeEnum contentType)
            throws IOException {
        handleRequest(builder, context);
        try {
            return handleResponse(execution.apply(builder), contentType);
        } finally {
            updateContext();
        }
    }

    /**
     * Pre-handle the uri.
     * On the client side, this step is performed before the request is
     * sent to the server. On the server side, this step is performed
     * on incoming messages before the message body is evaluated.
     *
     * @param builder the uri to preprocess
     * @param context the context for the request
     */
    protected void handleRequest(URIBuilder builder, HttpContext context) { }

    /**
     * Handle content of the response.
     *
     * @param content     content of the response
     * @param contentType type of the content
     * @return handled content
     * @throws HttpResponseException if the content contains an error
     */
    protected String handleResponse(String content, ContentTypeEnum contentType) throws HttpResponseException {
        return content;
    }

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
     * Execute the get request of the given uri and return content of response.
     */
    private String getContent(URIBuilder builder) throws IOException {
        String uri = builder.toString();
        log.info("Slept for {}s.", limiter.acquire());
        log.info("Get from {}", uri);
        HttpGet httpGet = new HttpGet(uri);
        return client.execute(httpGet, DEFAULT_RESPONSE_HANDLER, context);
    }

    /**
     * Execute the post request of the given uri and params.
     */
    private String postContent(URIBuilder builder, List<? extends NameValuePair> params) throws IOException {
        String uri = builder.toString();
        log.info("Slept for {}s.", postLimiter.acquire());
        log.info("Post from {}", uri);
        HttpPost httpPost = new HttpPost(uri);
        if (CollectionUtils.isNotEmpty(params)) {
            log.info("Params: {}", StringUtils.join(params, SignEnum.AND.getC()));
            httpPost.setEntity(new UrlEncodedFormEntity(params, Constants.UTF_8));
        }
        return client.execute(httpPost, DEFAULT_RESPONSE_HANDLER, context);
    }

    /**
     * Get the source by loading a web page in the current browser window.
     */
    private String loadContent(URIBuilder builder) {
        log.info("Slept for {}s.", limiter.acquire());
        updateToDriverCookies();
        chrome().get(builder.toString());
        String content = chrome().getPageSource();
        updateFromDriverCookies();
        return content;
    }

    private HttpClientContext readContext() {
        File file = cookieFile();
        HttpClientContext context = HttpClientContext.create();
        context.setRequestConfig(DEFAULT_REQUEST_CONFIG);
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

    private void updateContext() {
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

    private void updateFromDriverCookies() {
        CookieStore store = context.getCookieStore();
        for (org.openqa.selenium.Cookie cookie : chrome().manage().getCookies()) {
            BasicClientCookie clientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
            clientCookie.setPath(cookie.getPath());
            clientCookie.setDomain(cookie.getDomain());
            clientCookie.setExpiryDate(cookie.getExpiry());
            clientCookie.setSecure(cookie.isSecure());
            clientCookie.setAttribute("httpOnly", String.valueOf(cookie.isHttpOnly()));
            store.addCookie(clientCookie);
        }
    }

    private void updateToDriverCookies() {
        WebDriver.Options options = chrome().manage();
        for (Cookie clientCookie : context.getCookieStore().getCookies()) {
            options.addCookie(new org.openqa.selenium.Cookie
                    .Builder(clientCookie.getName(), clientCookie.getValue())
                    .domain(clientCookie.getDomain())
                    .path(clientCookie.getPath())
                    .expiresOn(clientCookie.getExpiryDate())
                    .isSecure(clientCookie.isSecure())
                    .build());
        }
    }

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
                .setHost(StringUtils.isBlank(subHost) ? host : subHost + host);
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

    protected final WebDriver chrome() {
        if (webDriver == null) {
            webDriver = new ChromeDriver();
        }
        return webDriver;
    }

    @Override
    public final void close() throws IOException {
        if (client != null) {
            client.close();
        }
        if (webDriver != null) {
            webDriver.close();
        }
    }
}
