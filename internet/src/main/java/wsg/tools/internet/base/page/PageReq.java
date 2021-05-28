package wsg.tools.internet.base.page;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;

/**
 * Interface for pagination information with a specified page size.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public interface PageReq extends PageIndex {

    /**
     * Creates a new page request.
     *
     * @param current zero-based page index, must not be negative.
     * @param size    the size of the page to be returned, must be greater than 0.
     * @return the page request
     */
    @Nonnull
    @Contract("_, _ -> new")
    static PageReq of(int current, int size) {
        return new PageRequest(current, size);
    }

    /**
     * Creates the first page request.
     *
     * @param size the size of the page to be returned, must be greater than 0.
     * @return the first page request
     */
    @Nonnull
    @Contract("_ -> new")
    static PageReq first(int size) {
        return new PageRequest(0, size);
    }

    /**
     * Returns the number of items to be returned.
     *
     * @return the number of items of that page
     */
    int getPageSize();

    /**
     * Returns the offset to be taken according to the underlying page and page size.
     *
     * @return the offset to be taken
     */
    long getOffset();

    /**
     * Returns the request requesting the next result.
     *
     * @return the next request
     */
    @Override
    PageReq next();

    /**
     * Returns the request requesting the previous result.
     *
     * @return the previous request, or the first request if the current one already is the first
     * one
     */
    @Override
    PageReq previous();
}
