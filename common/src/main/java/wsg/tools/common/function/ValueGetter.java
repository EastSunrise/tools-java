package wsg.tools.common.function;

/**
 * Supply a getter for one property of the given type.
 *
 * @author Kingen
 * @since 2020/7/21
 */
@FunctionalInterface
public interface ValueGetter<T, V> {

    /**
     * Obtains value from the given object
     *
     * @param t given object
     * @return obtained value
     */
    V getValue(T t);
}