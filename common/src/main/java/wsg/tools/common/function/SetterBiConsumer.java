package wsg.tools.common.function;

/**
 * Supply a setter for one property of the given type.
 *
 * @author Kingen
 * @since 2020/7/23
 */
@FunctionalInterface
public interface SetterBiConsumer<T, V> {

    /**
     * Set value.
     *
     * @param t target object
     * @param v value of property to set
     */
    void setValue(T t, V v);
}
