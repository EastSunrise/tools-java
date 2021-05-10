package wsg.tools.internet.movie.common;

import wsg.tools.internet.base.EntityProperty;

/**
 * Supplies the state of a item.
 *
 * @author Kingen
 * @since 2021/2/10
 */
@EntityProperty
public interface StateSupplier {

    /**
     * Returns the state.
     *
     * @return state
     */
    ResourceState getState();
}
