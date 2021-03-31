package wsg.tools.internet.base.page;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * A sublist of the list of all entities, containing information about the position of it in the
 * entire list.
 *
 * @param <T> type of the content the result contains
 * @param <P> type of the request for the result
 * @author Kingen
 * @since 2021/3/8
 */
public interface PageResult<T, P extends PageReq> {

    /**
     * Returns the zero-based index of the current result. Is always non-negative.
     *
     * @return the zero-based index of the current result.
     */
    int getCurrent();

    /**
     * Returns the size of the result.
     *
     * @return the size of the result.
     */
    int getPageSize();

    /**
     * Returns whether the result has content at all.
     *
     * @return whether the result has content at all
     */
    boolean hasContent();

    /**
     * Returns the page content as {@link List}.
     *
     * @return the page content as {@link List}
     */
    List<T> getContent();

    /**
     * Returns if there is a next result.
     *
     * @return if there is a next result.
     */
    boolean hasNext();

    /**
     * Returns if there is a previous result.
     *
     * @return if there is a previous result.
     */
    boolean hasPrevious();

    /**
     * Returns the request to request the next result. Clients should check {@link #hasNext()}
     * before calling this method.
     *
     * @return the next request
     * @throws NoSuchElementException if the current result is already the last one.
     */
    P nextPageRequest();

    /**
     * Returns the request to request the previous result. Clients should check {@link
     * #hasPrevious()} before calling this method.
     *
     * @return the previous request
     * @throws NoSuchElementException if the current result is already the first one.
     */
    P previousPageRequest();
}
