package wsg.tools.internet.base.support;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.base.PathSupplier;

/**
 * Builder for {@link HttpRequest}.
 * <p>
 * Since the {@code SiteSession} has contained scheme and main domain, so the builder can only
 * change sub domain, path, and query parameters to build a URI.
 *
 * @author Kingen
 * @since 2020/9/23
 */
public class RequestBuilder {

    private static final String PARAMETER_SEPARATOR = "&";

    private final String method;
    private final URIBuilder builder;
    private List<NameValuePair> parameters;
    private NameValuePair token;

    RequestBuilder(String method, URIBuilder builder) {
        this.method = method;
        this.builder = builder;
    }

    public String getMethod() {
        return method;
    }

    /**
     * Sets URI path.
     *
     * @param args args to format the path
     */
    public RequestBuilder setPath(String path, Object... args) {
        if (StringUtils.isNotBlank(path)) {
            builder.setPath(String.format(path, args));
        }
        return this;
    }

    /**
     * Adds a parameter to the request.
     */
    public RequestBuilder addParameter(NameValuePair pair) {
        Objects.requireNonNull(pair, "Name value pair");
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(pair);
        return this;
    }

    /**
     * Adds a parameter to the request. Do nothing if the value is {@literal null}.
     */
    public RequestBuilder addParameter(String name, Object value) {
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
     * Sets the parameter of the given name to the given value. Remove the previous value if
     * exists.
     */
    public RequestBuilder setParameter(String name, String value) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        if (!parameters.isEmpty()) {
            parameters.removeIf(pair -> pair.getName().equals(name));
        }
        parameters.add(new BasicNameValuePair(name, value));
        return this;
    }

    /**
     * Adds parameters to the request.
     */
    public RequestBuilder addParameters(NameValuePair... pairs) {
        for (NameValuePair pair : pairs) {
            addParameter(pair);
        }
        return this;
    }

    /**
     * Adds a token, which usually used in an api.
     */
    public void setToken(String name, String value) {
        this.token = new BasicNameValuePair(name, value);
    }

    @Override
    public String toString() {
        URIBuilder uriBuilder = new URIBuilder(URI.create(builder.toString()));
        if (parameters != null) {
            uriBuilder.addParameters(parameters);
        }
        return uriBuilder.toString();
    }

    public HttpUriRequest build() {
        HttpEntity entity = null;
        URI uri = URI.create(builder.toString());
        List<NameValuePair> params = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(parameters)) {
            params.addAll(parameters);
        }
        CollectionUtils.addIgnoreNull(params, token);
        if (CollectionUtils.isNotEmpty(params)) {
            if (HttpPost.METHOD_NAME.equalsIgnoreCase(method) || HttpPut.METHOD_NAME
                .equalsIgnoreCase(method)) {
                entity = new UrlEncodedFormEntity(params, HTTP.DEF_CONTENT_CHARSET);
            } else {
                uri = URI.create(new URIBuilder(uri).addParameters(params).toString());
            }
        }
        if (entity == null) {
            InternalRequest request = new InternalRequest(method);
            request.setURI(uri);
            return request;
        }
        HttpEntityEnclosingRequestBase request =
            HttpPost.METHOD_NAME.equalsIgnoreCase(method) ? new HttpPost() : new HttpPut();
        request.setEntity(entity);
        request.setURI(uri);
        return request;
    }

    /**
     * Construct a path for file system.
     */
    public String filepath() {
        StringBuilder sb = new StringBuilder();

        if (builder.isAbsolute()) {
            sb.append(File.separator).append(builder.getScheme());
        }

        String host = builder.getHost();
        String[] parts = host.split("\\.");
        for (int i = parts.length - 1; i >= 0; i--) {
            sb.append(File.separator).append(parts[i]);
        }

        if (!builder.isPathEmpty()) {
            for (String part : builder.getPathSegments()) {
                sb.append(File.separator).append(part);
            }
        }

        if (!builder.isQueryEmpty()) {
            sb.append(File.separator)
                .append(URLEncodedUtils.format(builder.getQueryParams(), Consts.UTF_8));
        }

        if (parameters != null) {
            sb.append(File.separator).append(StringUtils.join(parameters, PARAMETER_SEPARATOR));
        }

        return StringUtilsExt.toFilename(sb.toString());
    }

    private static class InternalRequest extends HttpRequestBase {

        private final String method;

        InternalRequest(String method) {
            super();
            this.method = method;
        }

        @Override
        public InternalRequest clone() throws CloneNotSupportedException {
            return (InternalRequest) super.clone();
        }

        @Override
        public String getMethod() {
            return method;
        }
    }
}
