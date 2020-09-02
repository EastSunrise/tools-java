package wsg.tools.internet.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.AssertUtils;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;

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
public abstract class BaseSite {

    protected static final String HTML_A = "a";
    protected static final String HTML_LI = "li";
    protected static final String HTML_SELECT = "select";
    protected static final String HTML_STRONG = "strong";
    protected static final String HTML_H1 = "h1";
    protected static final String HTML_TITLE = "title";
    protected static final String HTML_HREF = "href";
    private static final int TIME_OUT = 30000;
    private static final double DEFAULT_PERMIT_PER_SECOND = 10D;
    private static final int TIMEOUT_RETRY = 1;

    @Getter
    protected final String name;
    protected final SchemeEnum scheme;
    protected final String domain;
    private final RateLimiter limiter;
    protected ObjectMapper objectMapper;
    private CloseableHttpClient client;

    public BaseSite(String name, String domain) {
        this(name, SchemeEnum.HTTPS, domain, DEFAULT_PERMIT_PER_SECOND);
    }

    public BaseSite(String name, String domain, double permitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, permitsPerSecond);
    }

    public BaseSite(String name, SchemeEnum scheme, String domain, double permitsPerSecond) {
        this.name = name;
        this.scheme = scheme;
        this.domain = domain;
        this.limiter = RateLimiter.create(permitsPerSecond);
        this.setObjectMapper();
    }

    /**
     * Return the content of response in form of a parsed HTML document, using cached files.
     */
    protected Document getDocument(URI uri) throws HttpResponseException {
        return getDocument(uri, true);
    }

    /**
     * Return the content of response in form of a parsed HTML document.
     *
     * @param cached whether use the cached file.
     */
    protected Document getDocument(URI uri, boolean cached) throws HttpResponseException {
        if (cached) {
            return Jsoup.parse(getCachedContent(uri, ContentTypeEnum.HTML));
        } else {
            return Jsoup.parse(getContent(uri));
        }
    }

    /**
     * Return the content of response with a Java object.
     */
    protected <T> T getObject(URI uri, Class<T> clazz) throws HttpResponseException {
        return getObject(uri, clazz, true);
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected <T> T getObject(URI uri, TypeReference<T> type) throws HttpResponseException {
        return getObject(uri, type, true);
    }

    /**
     * Return the content of response with a Java object.
     */
    protected <T> T getObject(URI uri, Class<T> clazz, boolean cached) throws HttpResponseException {
        try {
            if (cached) {
                return objectMapper.readValue(getCachedContent(uri, ContentTypeEnum.JSON), clazz);
            } else {
                return objectMapper.readValue(getContent(uri), clazz);
            }
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected <T> T getObject(URI uri, TypeReference<T> type, boolean cached) throws HttpResponseException {
        try {
            if (cached) {
                return objectMapper.readValue(getCachedContent(uri, ContentTypeEnum.JSON), type);
            } else {
                return objectMapper.readValue(getContent(uri), type);
            }
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Get requested content. Read from files if exists
     */
    protected String getCachedContent(URI uri, ContentTypeEnum contentType) throws HttpResponseException {
        // read from file if exists
        String filepath = uri.toString().replaceAll("[:?]", "\\$");
        if (contentType != null) {
            filepath += contentType.getSuffix();
        }
        File file = new File(filepath);
        if (file.isFile()) {
            log.info("Read from {}", file.getPath());
            String content;
            try {
                content = FileUtils.readFileToString(file, UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            if (Constants.NULL_NA.equals(content)) {
                throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Not Found");
            }
            return content;
        }

        String data;
        try {
            data = getContent(uri);
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
        try {
            FileUtils.write(file, data, UTF_8);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
        return data;
    }

    /**
     * Do request and return content of response.
     */
    protected String getContent(URI uri) throws HttpResponseException {
        // do request
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(buildRequestConfig().build());
        log.info("Get from {}", uri.toString());
        httpGet.addHeader(HTTP.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
        CloseableHttpClient client = getClient();
        int timeoutRetry = 0;
        while (true) {
            limiter.acquire();
            try {
                HttpResponse response = client.execute(httpGet);
                int code = response.getStatusLine().getStatusCode();
                if (code == HttpStatus.SC_OK) {
                    return EntityUtils.toString(response.getEntity());
                }
                throw new HttpResponseException(code, response.getStatusLine().getReasonPhrase());
            } catch (HttpResponseException e) {
                throw e;
            } catch (SocketTimeoutException e) {
                if (timeoutRetry < TIMEOUT_RETRY) {
                    timeoutRetry++;
                    continue;
                }
                throw AssertUtils.runtimeException(e);
            } catch (IOException e) {
                throw AssertUtils.runtimeException(e);
            }
        }
    }

    private CloseableHttpClient getClient() {
        if (client == null) {
            client = HttpClients.createDefault();
        }
        return client;
    }

    protected RequestConfig.Builder buildRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(TIME_OUT).setSocketTimeout(TIME_OUT);
    }

    /**
     * Build base path of request uri, including scheme, host(domain by default), and path.
     * <p>
     * It's overridable to add customized parameters.
     */
    protected URIBuilder buildPath(String path, Object... pathArgs) {
        return new URIBuilder()
                .setScheme(scheme.toString())
                .setHost(domain)
                .setPath(String.format(path, pathArgs));
    }

    /**
     * Initialize {@link ObjectMapper}
     * <p>
     * Override it to customize Jackson if necessary.
     */
    protected void setObjectMapper() {
        objectMapper = new ObjectMapper();
    }
}
