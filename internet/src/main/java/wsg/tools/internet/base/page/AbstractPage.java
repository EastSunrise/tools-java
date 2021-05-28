package wsg.tools.internet.base.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This class provides a skeleton implementation of {@link Page}.
 *
 * @author Kingen
 * @since 2021/5/27
 */
public abstract class AbstractPage<T> implements Page<T>, Serializable {

    private static final long serialVersionUID = 1164886192858017617L;

    private final PageReq req;
    private final List<T> content = new ArrayList<>();

    /**
     * Constructs an instance of {@link Page}.
     *
     * @param content the content of this page, must not be {@literal null}.
     * @param req     the paging information, must not be {@literal null}.
     */
    protected AbstractPage(List<T> content, PageReq req) {
        this.req = Objects.requireNonNull(req, "PageRequest must not be null!");
        this.content.addAll(Objects.requireNonNull(content, "Content must not be null!"));
    }

    /**
     * Constructs an instance of {@link Page} with a specified size.
     *
     * @param content  the content of this page, must not be {@literal null}.
     * @param index    the paging index, must not be {@literal null}.
     * @param pageSize the standard size of the returned page, must be greater than 0.
     */
    protected AbstractPage(List<T> content, PageIndex index, int pageSize) {
        Objects.requireNonNull(index, "PageRequest must not be null!");
        this.req = new PageRequest(index.getCurrent(), pageSize);
        this.content.addAll(Objects.requireNonNull(content, "Content must not be null!"));
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
    public PageReq nextPageReq() {
        if (!hasNext()) {
            throw new NoSuchElementException("Doesn't have next page.");
        }
        return req.next();
    }

    @Override
    public PageReq previousPageReq() {
        if (!hasPrevious()) {
            throw new NoSuchElementException("Doesn't have previous page.");
        }
        return req.previous();
    }
}
