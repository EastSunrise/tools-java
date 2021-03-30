package wsg.tools.internet.resource.common;

import wsg.tools.internet.base.EntityInterface;

/**
 * Supplies the year of the item.
 *
 * @author Kingen
 * @since 2020/11/23
 */
@EntityInterface
public interface YearSupplier {

    /**
     * Obtains year.
     *
     * @return year
     */
    Integer getYear();
}