package wsg.tools.internet.base.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This class provides a skeletal implementation of {@link PageResult}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public abstract class AbstractPageResult<T, P extends PageReq> implements PageResult<T, P> {

    private final List<T> content = new ArrayList<>();
    private final P req;

    /**
     * Constructs an instance of {@link PageResult}.
     *
     * @param content the content of this page, must not be {@literal null}.
     * @param req     the paging information, must not be {@literal null}.
     */
    protected AbstractPageResult(List<T> content, P req) {
        Objects.requireNonNull(content, "Content must not be null!");
        Objects.requireNonNull(req, "PageRequest must not be null!");
        this.content.addAll(content);
        this.req = req;
    }

    @Override
    public int getCurrent() {
        return req.getCurrent();
    }

    @Override
    public int getPageSize() {
        return req.getPageSize();
    }

    @Override
    public boolean hasContent() {
        return !content.isEmpty();
    }

    @Override
    public List<T> getContent() {
        return Collections.unmodifiableList(content);
    }

    @Override
    public boolean hasPrevious() {
        return getCurrent() > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public P nextPageRequest() {
        if (!hasNext()) {
            throw new NoSuchElementException("Doesn't have next page.");
        }
        return (P) req.next();
    }

    @Override
    @SuppressWarnings("unchecked")
    public P previousPageRequest() {
        if (!hasPrevious()) {
            throw new NoSuchElementException("Doesn't have previous page.");
        }
        return (P) req.previous();
    }
}
