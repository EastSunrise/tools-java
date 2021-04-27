package wsg.tools.internet.common;

import org.apache.http.Header;
import wsg.tools.internet.base.ResponseWrapper;

/**
 * A basic implementation of {@link ResponseWrapper}.
 *
 * @author Kingen
 * @since 2021/4/9
 */
public class BasicResponseWrapper<T> implements ResponseWrapper<T> {

    private final Header[] headers;
    private final T content;

    public BasicResponseWrapper(Header[] headers, T content) {
        this.headers = headers;
        this.content = content;
    }

    @Override
    public Header[] getHeaders() {
        return headers;
    }

    @Override
    public T getContent() {
        return content;
    }
}
