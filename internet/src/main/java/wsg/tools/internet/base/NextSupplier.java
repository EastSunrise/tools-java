package wsg.tools.internet.base;

/**
 * Supplies the identifier of next record.
 *
 * @author Kingen
 * @since 2021/3/2
 */
@FunctionalInterface
public interface NextSupplier<T> {

    /**
     * Returns the index of next record.
     *
     * @return the index
     */
    T nextId();
}
