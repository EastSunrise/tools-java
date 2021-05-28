package wsg.tools.internet.base.page;

import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;

/**
 * A sublist of the list of all entities, containing information about the position of it in the
 * entire list.
 *
 * @param <T> type of the content the result contains
 * @author Kingen
 * @since 2021/3/8
 */
public interface Page<T> {

    /**
     * Constructs an instance of {@link Page} whose page amount is available.
     *
     * @param content    the content of this page, must not be {@literal null}.
     * @param req        the paging information, must not be {@literal null}.
     * @param totalPages the total amount of pages, must be positive
     * @return the page result
     */
    @Nonnull
    @Contract("_, _, _ -> new")
    static <V> CountablePage<V> countable(List<V> content, PageReq req, int totalPages) {
        return new CountablePageImpl<>(content, req, totalPages);
    }

    /**
     * Constructs an instance of {@link Page} whose page amount is available.
     *
     * @param content    the content of this page, must not be {@literal null}.
     * @param index      the paging index, must not be {@literal null}.
     * @param pageSize   the standard size of the returned page, must be greater than 0.
     * @param totalPages the total amount of pages, must be positive
     * @return the page result
     */
    @Nonnull
    @Contract("_, _, _, _ -> new")
    static <V> CountablePage<V>
    countable(List<V> content, PageIndex index, int pageSize, int totalPages) {
        return new CountablePageImpl<>(content, index, pageSize, totalPages);
    }

    /**
     * Constructs an instance of {@link Page} whose page amount is available.
     *
     * @param content the content of this page, must not be {@literal null}.
     * @param req     the paging information, must not be {@literal null}.
     * @param total   the total amount of elements available
     * @return the page result
     */
    @Nonnull
    @Contract("_, _, _ -> new")
    static <V> AmountCountablePage<V>
    amountCountable(List<V> content, PageReq req, long total) {
        return new AmountCountablePageImpl<>(content, req, total);
    }

    /**
     * Constructs an instance of {@link Page} whose page amount is available.
     *
     * @param content  the content of this page, must not be {@literal null}.
     * @param index    the paging index, must not be {@literal null}.
     * @param pageSize the standard size of the returned page, must be greater than 0.
     * @param total    the total amount of elements available
     * @return the page result
     */
    @Nonnull
    @Contract("_, _, _, _ -> new")
    static <V> AmountCountablePage<V>
    amountCountable(List<V> content, PageIndex index, int pageSize, long total) {
        return new AmountCountablePageImpl<>(content, index, pageSize, total);
    }

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
    PageReq nextPageReq();

    /**
     * Returns the request to request the previous result. Clients should check {@link
     * #hasPrevious()} before calling this method.
     *
     * @return the previous request
     * @throws NoSuchElementException if the current result is already the first one.
     */
    PageReq previousPageReq();
}
