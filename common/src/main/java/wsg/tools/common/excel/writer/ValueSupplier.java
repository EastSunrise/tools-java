package wsg.tools.common.excel.writer;

/**
 * Get value to write to cell from the given object.
 *
 * @author Kingen
 * @since 2020/7/21
 */
@FunctionalInterface
public interface ValueSupplier<T, V> {

    /**
     * Obtains value from the given object
     *
     * @param t given object
     * @return obtained value
     */
    V getValue(T t);
}
