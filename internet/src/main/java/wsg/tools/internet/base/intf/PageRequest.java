package wsg.tools.internet.base.intf;

import wsg.tools.internet.base.impl.BasicPageRequest;

/**
 * Interface for pagination information.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public interface PageRequest {

    /**
     * Obtains an instance of {@link PageRequest} with the given arguments.
     *
     * @param current  page index
     * @param pageSize page size
     * @return a paged request
     */
    static PageRequest of(int current, int pageSize) {
        return new BasicPageRequest(current, pageSize);
    }

    /**
     * Obtains an instance of first {@link PageRequest} with given page size.
     *
     * @param pageSize page size
     * @return a paged request
     */
    static PageRequest first(int pageSize) {
        return new BasicPageRequest(0, pageSize);
    }

    /**
     * Returns the page to be returned.
     *
     * @return the page to be returned
     */
    int getCurrent();

    /**
     * Returns the number of items to be returned.
     *
     * @return the number of items of that page
     */
    int getPageSize();

    /**
     * Returns the {@link PageRequest} requesting the next {@link PageResult}.
     *
     * @return the {@link PageRequest}
     */
    PageRequest next();

    /**
     * Returns the {@link PageRequest} requesting the previous {@link PageResult}.
     *
     * @return the {@link PageRequest}
     */
    PageRequest previous();
}
