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
abstract class AbstractPageResult<T> implements PageResult<T> {

    private final List<T> content = new ArrayList<>();
    private final PageRequest pageRequest;

    /**
     * Constructs an instance of {@link PageResult}.
     *
     * @param content     the content of this page, must not be {@literal null}.
     * @param pageRequest the paging information, must not be {@literal null}.
     */
    AbstractPageResult(List<T> content, PageRequest pageRequest) {
        Objects.requireNonNull(content, "Content must not be null!");
        Objects.requireNonNull(pageRequest, "PageRequest must not be null!");
        this.content.addAll(content);
        this.pageRequest = pageRequest;
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
            throw new NoSuchElementException("Doesn't have next page.");
        }
        return pageRequest.next();
    }

    @Override
    public PageRequest previousPageRequest() {
        if (!hasPrevious()) {
            throw new NoSuchElementException("Doesn't have previous page.");
        }
        return pageRequest.previous();
    }

}
