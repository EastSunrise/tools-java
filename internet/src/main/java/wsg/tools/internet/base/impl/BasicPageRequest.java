package wsg.tools.internet.base.impl;

import java.io.Serializable;
import wsg.tools.internet.base.intf.PageRequest;

/**
 * Basic implementation of {@link PageRequest}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class BasicPageRequest implements PageRequest, Serializable {

    private static final long serialVersionUID = -128117024824984036L;

    private final int current;
    private final int pageSize;

    /**
     * Creates an instance of paged request.
     *
     * @param current  zero-based page index, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     */
    public BasicPageRequest(int current, int pageSize) {
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
    public BasicPageRequest next() {
        return new BasicPageRequest(current + 1, getPageSize());
    }

    @Override
    public BasicPageRequest previous() {
        return current == 0 ? this : new BasicPageRequest(current - 1, getPageSize());
    }
}
