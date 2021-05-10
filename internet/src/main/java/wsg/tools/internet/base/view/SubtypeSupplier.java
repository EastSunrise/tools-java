package wsg.tools.internet.base.view;

import wsg.tools.internet.base.EntityProperty;

/**
 * Represents a supplier of the subtype to which the item belongs in the repository.
 *
 * @author Kingen
 * @since 2021/3/12
 */
@EntityProperty
public interface SubtypeSupplier<E extends Enum<E>> {

    /**
     * Returns the subtype to which the item belongs.
     *
     * @return the subtype
     */
    E getSubtype();
}
