package wsg.tools.internet.base;

/**
 * Supplies the identifier of next entity.
 *
 * @author Kingen
 * @since 2021/3/2
 */
@EntityInterface
public interface NextSupplier<ID> {

    /**
     * Returns the index of next record.
     *
     * @return the index
     */
    ID nextId();
}
