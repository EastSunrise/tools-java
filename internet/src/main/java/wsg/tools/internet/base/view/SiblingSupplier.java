package wsg.tools.internet.base.view;

import wsg.tools.internet.base.EntityProperty;

/**
 * Represents a supplier of the identifier of next entity as well as the identifier of previous
 * entity.
 *
 * @author Kingen
 * @since 2021/4/9
 */
@EntityProperty
public interface SiblingSupplier<ID> extends NextSupplier<ID> {

    /**
     * Returns the identifier of the previous entity.
     *
     * @return the previous identifier
     */
    ID getPreviousId();
}
