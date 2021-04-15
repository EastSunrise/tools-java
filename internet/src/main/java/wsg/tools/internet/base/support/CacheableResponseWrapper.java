package wsg.tools.internet.base.support;

import org.apache.http.Header;
import wsg.tools.internet.base.CacheResponseWrapper;

/**
 * A basic implementation of {@link CacheResponseWrapper}.
 *
 * @author Kingen
 * @since 2021/4/9
 */
public class CacheableResponseWrapper<T> extends BasicResponseWrapper<T> implements
    CacheResponseWrapper<T> {

    private final boolean snapshot;

    public CacheableResponseWrapper(Header[] headers, T content, boolean snapshot) {
        super(headers, content);
        this.snapshot = snapshot;
    }

    @Override
    public boolean isSnapshot() {
        return snapshot;
    }
}
