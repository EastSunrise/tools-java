package wsg.tools.common.util.function;

/**
 * Represents a setter, a function used to set a property of a bean to a value.
 *
 * @param <T> type of the bean one of whose property is set
 * @param <V> type of the value to which the property is set
 * @author Kingen
 * @since 2020/7/23
 */
@FunctionalInterface
public interface Setter<T, V> {

    /**
     * Sets a property of the bean to the value.
     *
     * @param bean  the bean whose property is set
     * @param value the value to which the property is set
     */
    void set(T bean, V value);
}
