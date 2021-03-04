package wsg.tools.internet.resource.common;

/**
 * Supply year of the item.
 *
 * @author Kingen
 * @since 2020/11/23
 */
@FunctionalInterface
public interface YearSupplier {

    /**
     * Obtains year.
     *
     * @return year
     */
    Integer getYear();
}