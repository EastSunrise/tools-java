package wsg.tools.internet.base.intf;

import java.util.List;
import wsg.tools.internet.base.impl.NoSuchPageException;

/**
 * A sublist of the list of all records, containing information about the position of it in the
 * entire list.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public interface PageResult<T> {

    /**
     * Returns the zero-based index of the current {@link PageResult}. Is always non-negative.
     *
     * @return the zero-based index of the current {@link PageResult}.
     */
    int getCurrent();

    /**
     * Returns the size of the {@link PageResult}.
     *
     * @return the size of the {@link PageResult}.
     */
    int getPageSize();

    /**
     * Returns the number of total pages.
     *
     * @return the number of total pages
     */
    int getTotalPages();

    /**
     * Returns whether the {@link PageResult} has content at all.
     *
     * @return whether the {@link PageResult} has content at all
     */
    boolean hasContent();

    /**
     * Returns the page content as {@link List}.
     *
     * @return the page content as {@link List}
     */
    List<T> getContent();

    /**
     * Returns if there is a next {@link PageRequest}.
     *
     * @return if there is a next {@link PageResult}.
     */
    boolean hasNext();

    /**
     * Returns if there is a previous {@link PageResult}.
     *
     * @return if there is a previous {@link PageResult}.
     */
    boolean hasPrevious();

    /**
     * Returns the {@link PageRequest} to request the next {@link PageResult}. Clients should check
     * {@link #hasNext()} before calling this method.
     *
     * @return the next {@link PageRequest}
     * @throws NoSuchPageException if the current {@link PageResult} is already the last one.
     */
    PageRequest nextPageRequest();

    /**
     * Returns the {@link PageRequest} to request the previous {@link PageResult}. Clients should
     * check {@link #hasPrevious()} before calling this method.
     *
     * @return the previous {@link PageRequest}
     * @throws NoSuchPageException if the current {@link PageResult} is already the first one.
     */
    PageRequest previousPageRequest();
}
