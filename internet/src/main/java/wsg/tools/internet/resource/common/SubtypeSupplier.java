package wsg.tools.internet.resource.common;

/**
 * Supplies the subtype that an item belongs to in the repository.
 *
 * @author Kingen
 * @since 2021/3/12
 */
@FunctionalInterface
public interface SubtypeSupplier<E extends Enum<E>> {

    /**
     * Returns the subtype that the item belongs to.
     *
     * @return the subtype
     */
    E getSubtype();
}
