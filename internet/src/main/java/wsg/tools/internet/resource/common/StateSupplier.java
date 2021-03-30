package wsg.tools.internet.resource.common;

import wsg.tools.internet.base.EntityInterface;

/**
 * Supply the state of the item.
 *
 * @author Kingen
 * @since 2021/2/10
 */
@EntityInterface
public interface StateSupplier {

    /**
     * Obtains the state.
     *
     * @return state
     */
    String getState();
}
