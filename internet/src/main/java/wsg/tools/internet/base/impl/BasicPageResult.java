package wsg.tools.internet.base.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import wsg.tools.internet.base.intf.PageRequest;
import wsg.tools.internet.base.intf.PageResult;

/**
 * Basic implementation of {@link PageResult}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class BasicPageResult<T> implements PageResult<T> {

    private final List<T> content = new ArrayList<>();
    private final PageRequest pageRequest;
    private final long total;

    /**
     * Constructs an instance of {@link PageResult}.
     *
     * @param content     the content of this page, must not be {@literal null}.
     * @param pageRequest the paging information, must not be {@literal null}.
     * @param total       the total amount of elements available.
     */
    public BasicPageResult(List<T> content, PageRequest pageRequest, long total) {
        Objects.requireNonNull(content, "Content must not be null!");
        Objects.requireNonNull(pageRequest, "PageRequest must not be null!");
        this.content.addAll(content);
        this.pageRequest = pageRequest;
        this.total = total;
    }

    @Override
    public int getCurrent() {
        return pageRequest.getCurrent();
    }

    @Override
    public int getPageSize() {
        return pageRequest.getPageSize();
    }

    @Override
    public int getTotalPages() {
        return (int) Math.ceil((double) total / (double) getPageSize());
    }

    @Override
    public long getTotalElements() {
        return total;
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
    public boolean hasNext() {
        return getCurrent() + 1 < getTotalPages();
    }

    @Override
    public boolean hasPrevious() {
        return getCurrent() > 0;
    }

    @Override
    public PageRequest nextPageRequest() {
        if (!hasNext()) {
            throw new NoSuchPageException("Doesn't have next page.");
        }
        return pageRequest.next();
    }

    @Override
    public PageRequest previousPageRequest() {
        if (!hasPrevious()) {
            throw new NoSuchPageException("Doesn't have previous page.");
        }
        return pageRequest.previous();
    }
}
