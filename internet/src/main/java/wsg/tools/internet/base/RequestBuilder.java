package wsg.tools.internet.base;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.StringUtilsExt;

import java.io.File;
import java.util.List;

/**
 * A constructor to build a {@link org.apache.http.HttpRequest}.
 *
 * @author Kingen
 * @since 2020/9/23
 */
public class RequestBuilder extends HttpRequestBase {

    private static final String PARAMETER_SEPARATOR = "&";

    private final String method;
    private final URIBuilder builder;
    private final List<? extends NameValuePair> params;

    private RequestBuilder(String method, URIBuilder builder, List<? extends NameValuePair> params) {
        this.method = method;
        this.builder = builder;
        this.params = params;
    }

    /**
     * Build a {@link HttpGet}.
     */
    public static RequestBuilder get(URIBuilder builder) {
        return new RequestBuilder(HttpGet.METHOD_NAME, builder, null);
    }

    /**
     * Build a {@link HttpPost}.
     */
    public static RequestBuilder post(URIBuilder builder, List<? extends NameValuePair> params) {
        return new RequestBuilder(HttpPost.METHOD_NAME, builder, params);
    }

    /**
     * Add an api key to the request if required.
     */
    public void addToken(final String name, final String token) {
        builder.setParameter(name, token);
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
                if (!"".equals(part)) {
                    sb.append(File.separator).append(part);
                }
            }
        }

        if (!builder.isQueryEmpty()) {
            sb.append(File.separator).append(URLEncodedUtils.format(builder.getQueryParams(), Consts.UTF_8));
        }

        if (params != null) {
            sb.append(File.separator).append(StringUtils.join(params, PARAMETER_SEPARATOR));
        }

        return StringUtilsExt.toFilename(sb.toString());
    }

    /**
     * Construct a url to display.
     */
    public String displayUrl() {
        String url = builder.toString();
        if (params != null) {
            url += ", params: " + StringUtils.join(params, PARAMETER_SEPARATOR);
        }
        return url;
    }

    public HttpRequestBase build() {
        if (HttpGet.METHOD_NAME.equals(method)) {
            return new HttpGet(builder.toString());
        }
        if (HttpPost.METHOD_NAME.equals(method)) {
            HttpPost post = new HttpPost(builder.toString());
            if (CollectionUtils.isNotEmpty(params)) {
                post.setEntity(new UrlEncodedFormEntity(params, Constants.UTF_8));
            }
            return post;
        }
        throw new IllegalArgumentException("Unknown method of request: " + method);
    }

    @Override
    public String getMethod() {
        return method;
    }
}
