package wsg.tools.internet.resource.common;

import wsg.tools.internet.base.EntityInterface;

/**
 * Supplies the state of a item.
 * <p>
 * todo update snapshots
 *
 * @author Kingen
 * @since 2021/2/10
 */
@EntityInterface
public interface StateSupplier {

    /**
     * Returns the state.
     *
     * @return state
     */
    String getState();
}
