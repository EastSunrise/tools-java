package wsg.tools.internet.base.support;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.base.view.PathSupplier;

/**
 * A wrapper of a request builder, the target host and a token if required.
 *
 * @author Kingen
 * @since 2020/9/23
 */
public class RequestWrapper {

    private static final String PARAMETER_SEPARATOR = "&";

    private final RequestBuilder builder;
    private final HttpHost httpHost;
    private NameValuePair token;

    RequestWrapper(RequestBuilder builder, HttpHost httpHost) {
        this.builder = builder;
        this.httpHost = httpHost;
    }

    public HttpHost getHttpHost() {
        return httpHost;
    }

    public URI getUri() {
        if (httpHost == null) {
            return builder.getUri();
        }
        return URI.create(httpHost.toURI()).resolve(builder.getUri());
    }

    /**
     * Sets the header of the request.
     */
    public RequestWrapper setHeader(Header header) {
        builder.setHeader(header);
        return this;
    }

    /**
     * Sets the header of the request.
     */
    public RequestWrapper setHeader(String name, String value) {
        setHeader(new BasicHeader(name, value));
        return this;
    }

    /**
     * Adds a parameter to the request.
     */
    public RequestWrapper addParameter(NameValuePair pair) {
        Objects.requireNonNull(pair, "Name value pair");
        builder.addParameter(pair);
        return this;
    }

    /**
     * Adds a parameter to the request.
     */
    public RequestWrapper addParameter(String name, Object value) {
        if (value == null) {
            return this;
        }
        if (value instanceof PathSupplier) {
            addParameter(new BasicNameValuePair(name, ((PathSupplier) value).getAsPath()));
        } else {
            addParameter(new BasicNameValuePair(name, value.toString()));
        }
        return this;
    }

    /**
     * Sets the configuration of the request.
     */
    public RequestWrapper setConfig(RequestConfig config) {
        builder.setConfig(config);
        return this;
    }

    /**
     * Adds a token, which usually used in an api.
     */
    public RequestWrapper setToken(String name, String value) {
        this.token = new BasicNameValuePair(name, value);
        return this;
    }

    public HttpUriRequest build() {
        RequestBuilder copy = builder;
        if (token != null) {
            copy = RequestBuilder.copy(builder.build());
            copy.addParameter(token);
        }
        return copy.build();
    }

    /**
     * Construct a path for file system.
     */
    public String filepath() {
        StringBuilder sb = new StringBuilder();
        URI uri = getUri();
        if (uri.isAbsolute()) {
            sb.append(File.separator).append(uri.getScheme());
        }

        String host = uri.getHost();
        String[] parts = host.split("\\.");
        for (int i = parts.length - 1; i >= 0; i--) {
            sb.append(File.separator).append(parts[i]);
        }

        String path = uri.getPath();
        if (path != null) {
            sb.append(path);
        }

        String query = uri.getQuery();
        if (query != null) {
            sb.append(File.separator).append(query);
        }

        List<NameValuePair> parameters = builder.getParameters();
        if (!parameters.isEmpty()) {
            sb.append(File.separator).append(StringUtils.join(parameters, PARAMETER_SEPARATOR));
        }

        return StringUtilsExt.toFilename(sb.toString());
    }
}
