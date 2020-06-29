package wsg.tools.boot.config.thymeleaf.expression;

import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

/**
 * Customized Utility.
 *
 * @author Kingen
 * @since 2020/6/25
 */
public final class Customs {

    /**
     * Update parameters of the request
     *
     * @return string of request url
     */
    public String updateParameters(final String uri, final String name, final Object value) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(uri);
        if (value != null) {
            builder.setParameter(name, value.toString());
        }
        return builder.build().toString();
    }
}
