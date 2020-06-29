package wsg.tools.internet.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Consts;
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
import wsg.tools.common.jackson.config.BaseJacksonConfig;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Base class for a website.
 * <p>
 * Relative fundamental methods are defined inside to help request from the website.
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
public abstract class BaseSite {

    protected static final String HTML_A = "a";
    protected static final String HTML_SELECT = "select";

    @Getter
    private String name;
    private SchemeEnum scheme = SchemeEnum.HTTPS;
    private String domain;
    private int interval;
    @Setter
    private int timeout = 30000;

    private ObjectMapper objectMapper;
    private CloseableHttpClient client;
    private long lastAccess = 0L;

    public BaseSite(String name, String domain, int interval) {
        this.name = name;
        this.domain = domain;
        this.interval = interval;
    }

    /**
     * Return the content of response in form of a parsed HTML document.
     */
    protected Document getDocument(URI uri) throws IOException {
        return Jsoup.parse(getCachedContent(uri, ContentTypeEnum.HTML));
    }

    /**
     * Return the content of response with a Java object.
     */
    protected <T> T getObject(URI uri, Class<T> clazz) throws IOException {
        return getObjectMapper().readValue(getCachedContent(uri, ContentTypeEnum.JSON), clazz);
    }

    /**
     * Get requested content. Read from files if exists
     */
    protected String getCachedContent(URI uri, ContentTypeEnum contentType) throws IOException {
        // read from file if exists
        String filepath = uri.toString().replaceAll("[:?]", "\\$");
        if (contentType != null) {
            filepath += contentType.getSuffix();
        }
        File file = new File(filepath);
        if (file.isFile()) {
            log.info("Read from {}", file.getPath());
            String content = FileUtils.readFileToString(file, Consts.UTF_8);
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
                FileUtils.write(file, Constants.NULL_NA, Consts.UTF_8);
            }
            throw e;
        }
        FileUtils.write(file, data, Consts.UTF_8);
        return data;
    }

    /**
     * Do request and return content of response.
     */
    protected String getContent(URI uri) throws IOException {
        // do request
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(builderRequestConfig().build());
        log.info("Get from {}", uri.toString());
        httpGet.addHeader(HTTP.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
        CloseableHttpClient client = getClient();
        nextAccess();
        HttpResponse response = client.execute(httpGet);
        int code = response.getStatusLine().getStatusCode();
        if (code == HttpStatus.SC_OK) {
            return EntityUtils.toString(response.getEntity());
        }
        throw new HttpResponseException(code, response.getStatusLine().getReasonPhrase());
    }

    /**
     * Open a uri with the default browser of the system.
     */
    protected void openUri(URI uri) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri);
                }
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private CloseableHttpClient getClient() {
        if (client == null) {
            client = HttpClients.createDefault();
        }
        return client;
    }

    protected RequestConfig.Builder builderRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(timeout);
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
                uriBuilder.addParameter(pair.getKey(), pair.getValue());
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
            objectMapper = BaseJacksonConfig.objectMapper();
        }
        return objectMapper;
    }

    private void nextAccess() {
        if (interval == 0) {
            return;
        }
        long waiting = interval * 1000 + lastAccess - System.currentTimeMillis();
        if (waiting > 0) {
            try {
                Thread.sleep(waiting);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastAccess = System.currentTimeMillis();
    }

    protected static class Parameter extends MutablePair<String, String> {

        private Parameter(final String left, final String right) {
            super(left, right);
        }

        public static Parameter of(final String left, final String right) {
            return new Parameter(left, right);
        }
    }
}
