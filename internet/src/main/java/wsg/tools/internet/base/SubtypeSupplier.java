package wsg.tools.internet.base;

/**
 * Indicates the subtype that an item belongs to in the repository.
 *
 * @author Kingen
 * @since 2021/3/12
 */
@EntityInterface
public interface SubtypeSupplier {

    /**
     * Returns the subtype that the item belongs to.
     *
     * @return the subtype
     */
    int getSubtype();
}
