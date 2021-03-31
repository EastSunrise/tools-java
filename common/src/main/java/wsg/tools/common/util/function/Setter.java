package wsg.tools.common.util.function;

/**
 * A function to set a value to a bean.
 *
 * @param <T> type of the bean to which the value is set
 * @param <V> type of the value to be set
 * @author Kingen
 * @since 2020/7/23
 */
@FunctionalInterface
public interface Setter<T, V> {

    /**
     * Sets the value to the given bean.
     *
     * @param bean  the bean to which the value is set
     * @param value the value to be set
     */
    void set(T bean, V value);
}
