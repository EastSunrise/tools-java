package wsg.tools.internet.base.view;

import wsg.tools.internet.base.EntityInterface;

/**
 * Represents a supplier of the identifier of next entity.
 *
 * @author Kingen
 * @since 2021/3/2
 */
@EntityInterface
public interface NextSupplier<ID> {

    /**
     * Returns the identifier of next entity.
     *
     * @return the identifier
     */
    ID nextId();
}
