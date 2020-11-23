package wsg.tools.internet.resource.entity.item.base;

import javax.annotation.Nullable;

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
     * @return year, may null
     */
    @Nullable
    Integer getYear();
}