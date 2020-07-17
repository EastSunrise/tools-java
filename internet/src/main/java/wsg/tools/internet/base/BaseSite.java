package wsg.tools.internet.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

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
    protected static final String HTML_H1 = "h1";
    protected static final String HTML_TITLE = "title";
    private static final int TIME_OUT = 30000;

    @Getter
    private String name;
    private SchemeEnum scheme = SchemeEnum.HTTPS;
    private String domain;

    private ObjectMapper objectMapper;
    private CloseableHttpClient client;
    private RateLimiter limiter;

    public BaseSite(String name, String domain) {
        this(name, domain, 10D);
    }

    public BaseSite(String name, String domain, double permitsPerSecond) {
        this.name = name;
        this.domain = domain;
        this.limiter = RateLimiter.create(permitsPerSecond);
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
        try {
            return getObjectMapper().readValue(getCachedContent(uri, ContentTypeEnum.JSON), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return the content of response with a generic Java object.
     */
    protected <T> T getObject(URI uri, TypeReference<T> type) throws HttpResponseException {
        try {
            return getObjectMapper().readValue(getCachedContent(uri, ContentTypeEnum.JSON), type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
                throw new RuntimeException(e);
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
                    throw new RuntimeException(ex);
                }
            }
            throw e;
        }
        try {
            FileUtils.write(file, data, UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpGet.releaseConnection();
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
     * Build URI of request.
     * <p>
     * It's overridable to apply customized rules.
     */
    protected URIBuilder buildUri(String path, String lowDomain, Parameter... params) {
        URIBuilder uriBuilder = new URIBuilder().setScheme(scheme.toString()).setPath(path);
        if (params != null) {
            for (Pair<String, String> pair : params) {
                if (pair != null) {
                    uriBuilder.addParameter(pair.getKey(), pair.getValue());
                }
            }
        }
        String host = domain;
        if (StringUtils.isNotBlank(lowDomain)) {
            host = lowDomain + "." + host;
        }
        uriBuilder.setHost(host);
        return uriBuilder;
    }

    /**
     * Initialize {@link ObjectMapper}
     * <p>
     * Override it to customize Jackson if necessary.
     */
    public ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    protected static class Parameter extends MutablePair<String, String> {

        private Parameter(final String left, final String right) {
            super(left, right);
        }

        public static Parameter of(final String left, final Object right) {
            Objects.requireNonNull(left);
            Objects.requireNonNull(right);
            return new Parameter(left, right.toString());
        }
    }
}
