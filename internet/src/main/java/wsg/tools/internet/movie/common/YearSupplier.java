package wsg.tools.internet.movie.common;

import wsg.tools.internet.base.EntityInterface;

/**
 * Represents a supplier of the year of the entity.
 *
 * @author Kingen
 * @since 2020/11/23
 */
@EntityInterface
public interface YearSupplier {

    /**
     * Returns the year of the entity.
     *
     * @return the year
     */
    Integer getYear();
}