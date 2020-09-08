package wsg.tools.internet.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignConstants;
import wsg.tools.common.util.AssertUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
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

    protected static final String HTML_A = "a";
    protected static final String HTML_LI = "li";
    protected static final String HTML_STRONG = "strong";
    protected static final String HTML_TITLE = "title";
    protected static final String HTML_HREF = "href";
    protected static final Set<String> USELESS_TAGS = Set.of(
            "link", "style", "img", "br"
    );
    protected static final int CONNECT_TIME_OUT = 15000;
    protected static final int SOCKET_TIME_OUT = 15000;
    private static final double DEFAULT_PERMIT_PER_SECOND = 10D;
    private static final Collection<? extends Header> DEFAULT_HEADERS = Collections.singletonList(
            new BasicHeader(HTTP.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"));
    private static final RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(CONNECT_TIME_OUT).setSocketTimeout(SOCKET_TIME_OUT).build();
    private static final ResponseHandler<String> DEFAULT_RESPONSE_HANDLER = new BasicResponseHandler();

    @Getter
    protected final String name;
    protected final SchemeEnum scheme;
    protected final String domain;
    protected final ObjectMapper objectMapper;
    private final HttpClientContext localHttpContext;
    private final RateLimiter limiter;
    private final CloseableHttpClient httpClient;
    private WebDriver webDriver;
    @Setter
    private String cdn;

    public BaseSite(String name, String domain) {
        this(name, domain, DEFAULT_PERMIT_PER_SECOND);
    }

    public BaseSite(String name, String domain, double permitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, permitsPerSecond);
    }

    public BaseSite(String name, SchemeEnum scheme, String domain, double permitsPerSecond) {
        this.name = name;
        this.scheme = scheme;
        this.domain = domain;
        this.objectMapper = objectMapper();
        this.limiter = RateLimiter.create(permitsPerSecond);
        this.localHttpContext = new HttpClientContext();
        this.httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(DEFAULT_REQUEST_CONFIG)
                .setDefaultHeaders(DEFAULT_HEADERS)
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
    }

    protected Document getDocument(URIBuilder builder) throws IOException {
        return getDocument(builder, true, false);
    }

    /**
     * Return the content of response in form of a parsed HTML document.
     *
     * @param cached whether use the cached file.
     * @param loaded load by chromedriver if true, otherwise request by httpclient.
     * @throws IOException only thrown when not loaded
     */
    protected Document getDocument(URIBuilder builder, boolean cached, boolean loaded) throws IOException {
        if (cached) {
            return Jsoup.parse(readCachedContent(builder, ContentTypeEnum.HTML, loaded));
        }
        if (loaded) {
            return Jsoup.parse(loadContent(builder));
        }
        return Jsoup.parse(requestContent(builder));
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
            if (cached) {
                return objectMapper.readValue(handleJsonAfterReading(readCachedContent(builder, ContentTypeEnum.JSON, false)), clazz);
            } else {
                return objectMapper.readValue(handleJsonAfterReading(requestContent(builder)), clazz);
            }
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected <T> T getObject(URIBuilder builder, TypeReference<T> type, boolean cached) throws IOException {
        try {
            if (cached) {
                return objectMapper.readValue(handleJsonAfterReading(readCachedContent(builder, ContentTypeEnum.JSON, false)), type);
            } else {
                return objectMapper.readValue(handleJsonAfterReading(requestContent(builder)), type);
            }
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

    /**
     * Get requested content. Read from the corresponding cached file if exists.
     *
     * @param loaded load by chromedriver if true, otherwise request by httpclient.
     * @throws IOException only thrown when not loaded
     */
    private String readCachedContent(URIBuilder builder, ContentTypeEnum contentType, boolean loaded) throws IOException {
        File file = new File(filepath(builder, contentType));
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
            if (loaded) {
                content = loadContent(builder);
            } else {
                content = requestContent(builder);
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
     */
    private String filepath(URIBuilder uriBuilder, ContentTypeEnum contentType) {
        URI uri;
        try {
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
        StringBuilder builder = new StringBuilder();
        if (cdn != null) {
            builder.append(cdn);
        }

        String scheme = uri.getScheme();
        Objects.requireNonNull(scheme);
        builder.append(File.separator).append(scheme);

        String host = uri.getHost();
        Objects.requireNonNull(host);
        String[] parts = host.split("\\.");
        for (int i = parts.length - 1; i >= 0; i--) {
            builder.append(File.separator).append(parts[i]);
        }

        String path = uri.getPath();
        Objects.requireNonNull(path);
        parts = path.split(SignConstants.SLASH);
        for (String part : parts) {
            if (!"".equals(part)) {
                builder.append(File.separator).append(part);
            }
        }

        String query = uri.getQuery();
        if (query != null) {
            builder.append(File.separator).append(query);
        }

        if (contentType != null) {
            builder.append(contentType.getSuffix());
        }

        return builder.toString();
    }

    /**
     * Pre-handle content of html after requesting or loading and before writing to the cached file.
     */
    protected String handleHtmlBeforeCaching(String html) {
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
     * Execute the request of the given uri and return content of response.
     */
    private String requestContent(URIBuilder builder) throws IOException {
        URI uri;
        try {
            uri = addToken(builder).build();
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
        log.info("Get from {}", uri.toString());
        HttpGet httpGet = new HttpGet(uri);
        limiter.acquire();
        return httpClient.execute(httpGet, DEFAULT_RESPONSE_HANDLER, localHttpContext);
    }

    /**
     * Get the source by loading a web page in the current browser window.
     */
    private String loadContent(URIBuilder builder) {
        URI uri;
        try {
            uri = addToken(builder).build();
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
        limiter.acquire();
        chrome().get(uri.toString());
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

    private WebDriver chrome() {
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
