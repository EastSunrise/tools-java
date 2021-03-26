package wsg.tools.internet.base.page;

/**
 * A pageable result with countable total elements.
 *
 * @author Kingen
 * @since 2021/3/26
 */
public interface CountablePageResult<T> extends PageResult<T> {

    /**
     * Returns the total amount of elements.
     *
     * @return the total amount of elements
     */
    long getTotalElements();
}
