package wsg.tools.internet.base.page;

import java.io.Serializable;

/**
 * Basic implementation of {@link PageReq}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class BasicPageReq implements PageReq, Serializable {

    private static final long serialVersionUID = -128117024824984036L;

    private final int current;
    private final int pageSize;

    /**
     * Creates an instance of paged request.
     *
     * @param current  zero-based page index, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     */
    public BasicPageReq(int current, int pageSize) {
        if (current < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        }
        this.current = current;
        this.pageSize = pageSize;
    }

    @Override
    public int getCurrent() {
        return current;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public BasicPageReq next() {
        return new BasicPageReq(current + 1, pageSize);
    }

    @Override
    public BasicPageReq previous() {
        return current == 0 ? this : new BasicPageReq(current - 1, pageSize);
    }
}
