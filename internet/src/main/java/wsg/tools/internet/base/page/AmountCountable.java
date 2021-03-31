package wsg.tools.internet.base.page;

/**
 * Indicates that the amount of total elements is available.
 *
 * @author Kingen
 * @since 2021/3/31
 */
public interface AmountCountable {

    /**
     * Returns the total amount of elements.
     *
     * @return the total amount of elements
     */
    long getTotalElements();
}
