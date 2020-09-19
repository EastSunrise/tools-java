package wsg.tools.internet.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.function.throwable.ThrowableFunction;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.StringUtilsExt;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Base class for a website.
 * <p>
 * Relative fundamental methods are defined inside to help request from the website.
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
    protected static final String TAG_DL = "dl";
    protected static final String ATTR_HREF = "href";
    protected static final Set<String> USELESS_TAGS = Set.of(
            "link", "style", "img", "br"
    );

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

    @Getter
    protected final String name;
    protected final SchemeEnum scheme;
    protected final String domain;
    protected final ObjectMapper objectMapper;
    private final HttpClientContext localHttpContext;
    private final RateLimiter limiter;
    private final RateLimiter postLimiter;
    private final CloseableHttpClient httpClient;
    private WebDriver webDriver;
    @Setter
    private String cdn;

    public BaseSite(String name, String domain) {
        this(name, domain, DEFAULT_PERMIT_PER_SECOND);
    }

    public BaseSite(String name, String domain, double permitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, permitsPerSecond, permitsPerSecond);
    }

    public BaseSite(String name, SchemeEnum scheme, String domain, double permitsPerSecond, double postPermitsPerSecond) {
        this.name = name;
        this.scheme = scheme;
        this.domain = domain;
        this.objectMapper = objectMapper();
        this.limiter = RateLimiter.create(permitsPerSecond);
        this.postLimiter = RateLimiter.create(postPermitsPerSecond);
        this.localHttpContext = new HttpClientContext();
        this.httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(DEFAULT_REQUEST_CONFIG)
                .setDefaultHeaders(DEFAULT_HEADERS)
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
    }

    /**
     * Return the content of html content of post request.
     */
    protected Document postDocument(URIBuilder builder, final List<BasicNameValuePair> params, boolean cached) throws IOException {
        try {
            String html;
            if (cached) {
                html = readCachedContent(builder, ContentTypeEnum.HTML, b -> postContent(b, params), params);
            } else {
                html = postContent(builder, params);
            }
            return Jsoup.parse(html);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the document of html content of get request.
     */
    protected Document getDocument(URIBuilder builder, boolean cached) throws IOException {
        try {
            String html;
            if (cached) {
                html = readCachedContent(builder, ContentTypeEnum.HTML, this::getContent);
            } else {
                html = getContent(builder);
            }
            return Jsoup.parse(html);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the document of html content loaded by webdriver.
     */
    protected Document loadDocument(URIBuilder builder, boolean cached) throws IOException {
        try {
            String html;
            if (cached) {
                html = readCachedContent(builder, ContentTypeEnum.HTML, this::loadContent);
            } else {
                html = loadContent(builder);
            }
            return Jsoup.parse(html);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the content of response with a Java object.
     */
    protected <T> T getObject(URIBuilder builder, Class<T> clazz) throws IOException {
        return getObject(builder, clazz, true);
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected <T> T getObject(URIBuilder builder, TypeReference<T> type) throws IOException {
        return getObject(builder, type, true);
    }

    /**
     * Return the content of response with a Java object.
     */
    protected <T> T getObject(URIBuilder builder, Class<T> clazz, boolean cached) throws IOException {
        try {
            String json;
            if (cached) {
                json = readCachedContent(builder, ContentTypeEnum.JSON, this::getContent);
            } else {
                json = getContent(builder);
            }
            return objectMapper.readValue(handleJsonAfterReading(json), clazz);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected <T> T getObject(URIBuilder builder, TypeReference<T> type, boolean cached) throws IOException {
        try {
            String json;
            if (cached) {
                json = readCachedContent(builder, ContentTypeEnum.JSON, this::getContent);
            } else {
                json = getContent(builder);
            }
            return objectMapper.readValue(handleJsonAfterReading(json), type);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Pre-handle content of JSON after reading cached or not content and before convert to the target object.
     */
    protected String handleJsonAfterReading(String json) throws JsonProcessingException, HttpResponseException {
        return json;
    }

    private String readCachedContent
            (URIBuilder builder, ContentTypeEnum contentType, ThrowableFunction<URIBuilder, String, IOException> function)
            throws IOException {
        return readCachedContent(builder, contentType, function, null);
    }

    /**
     * Read content from the corresponding cached file if exists.
     * Otherwise acquire content and save to file system.
     *
     * @param function function to acquire content by the given uri.
     * @throws IOException only thrown when not loaded
     */
    private String readCachedContent(
            URIBuilder builder, ContentTypeEnum contentType, ThrowableFunction<URIBuilder, String, IOException> function,
            List<BasicNameValuePair> params) throws IOException {
        File file = new File(filepath(builder, contentType, params));
        if (file.isFile()) {
            log.info("Read from {}", file.getPath());
            String content;
            try {
                content = FileUtils.readFileToString(file, UTF_8);
            } catch (IOException e) {
                throw AssertUtils.runtimeException(e);
            }
            if (Constants.NULL_NA.equals(content)) {
                throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Not Found");
            }
            return content;
        }

        String content;
        try {
            content = function.apply(builder);
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
        if (ContentTypeEnum.HTML.equals(contentType)) {
            content = handleHtmlBeforeCaching(content);
        }
        try {
            FileUtils.write(file, content, UTF_8);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
        return content;
    }

    /**
     * Build path of cached file by uri and type of content.
     *
     * @param params when posting
     */
    private String filepath(URIBuilder uriBuilder, ContentTypeEnum contentType, List<BasicNameValuePair> params) {
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

        if (contentType != null) {
            builder.append(contentType.getSuffix());
        }

        String filepath = StringUtilsExt.toFilename(builder.toString());
        if (cdn != null) {
            filepath = cdn + filepath;
        }
        return filepath;
    }

    /**
     * Pre-handle content of html after requesting or loading and before writing to the cached file.
     */
    protected String handleHtmlBeforeCaching(String html) throws IOException {
        Document document = Jsoup.parse(html);
        handleNode(document);
        return document.html();
    }

    /**
     * Remove useless tags, blank texts, and comments.
     */
    private void handleNode(Node root) {
        int size = root.childNodeSize();
        for (int i = 0; i < size; i++) {
            Node child = root.childNode(i);
            boolean removed = false;
            if (child instanceof Element) {
                Element element = (Element) child;
                String tagName = element.tagName();
                if (USELESS_TAGS.contains(tagName)) {
                    removed = true;
                } else if ("script".equals(tagName)) {
                    if ("text/javascript".equals(element.attr("type"))) {
                        removed = true;
                    }
                }
            } else if (child instanceof TextNode) {
                if (((TextNode) child).isBlank()) {
                    removed = true;
                }
            } else if (child instanceof Comment) {
                removed = true;
            }

            if (removed) {
                child.remove();
                i--;
                size--;
                continue;
            }
            handleNode(child);
        }
    }

    /**
     * Execute the get request of the given uri and return content of response.
     */
    private String getContent(URIBuilder builder) throws IOException {
        String uri = addToken(builder).toString();
        log.info("Slept for {}s.", limiter.acquire());
        log.info("Get from {}", uri);
        HttpGet httpGet = new HttpGet(uri);
        return httpClient.execute(httpGet, DEFAULT_RESPONSE_HANDLER, localHttpContext);
    }

    /**
     * Execute the post request of the given uri and params.
     */
    protected String postContent(URIBuilder builder, List<? extends NameValuePair> params) throws IOException {
        String uri = builder.toString();
        log.info("Slept for {}s.", postLimiter.acquire());
        log.info("Post from {}", uri);
        HttpPost httpPost = new HttpPost(uri);
        if (CollectionUtils.isNotEmpty(params)) {
            log.info("Params: {}", StringUtils.join(params, SignEnum.AND.getC()));
            httpPost.setEntity(new UrlEncodedFormEntity(params, Constants.UTF_8));
        }
        return httpClient.execute(httpPost, DEFAULT_RESPONSE_HANDLER, localHttpContext);
    }

    /**
     * Get the source by loading a web page in the current browser window.
     */
    private String loadContent(URIBuilder builder) {
        log.info("Slept for {}s.", limiter.acquire());
        chrome().get(builder.toString());
        return chrome().getPageSource();
    }

    /**
     * Get builder of request uri, including scheme, host(domain by default), and path.
     */
    protected URIBuilder uriBuilder(String path, Object... pathArgs) {
        return new URIBuilder()
                .setScheme(scheme.toString())
                .setHost(domain)
                .setPath(String.format(path, pathArgs));
    }

    /**
     * Get builder of request uri with low domain.
     */
    protected URIBuilder withLowDomain(String lowDomain, String path, Object... pathArgs) {
        return new URIBuilder()
                .setScheme(scheme.toString())
                .setHost(lowDomain + "." + domain)
                .setPath(String.format(path, pathArgs));
    }

    /**
     * Add token before requesting if necessary.
     */
    protected URIBuilder addToken(URIBuilder builder) {
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

    protected WebDriver chrome() {
        if (webDriver == null) {
            webDriver = new ChromeDriver();
        }
        return webDriver;
    }

    @Override
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
        if (webDriver != null) {
            webDriver.close();
        }
    }
}
