package wsg.tools.common.util.function;

/**
 * A function to get a value from a bean.
 *
 * @param <T> type of the bean from which the value is gotten
 * @param <V> type of the gotten value
 * @author Kingen
 * @since 2020/7/21
 */
@FunctionalInterface
public interface Getter<T, V> {

    /**
     * Gets a value from the given bean.
     *
     * @param bean the bean from which a value is gotten
     * @return gotten value
     */
    V get(T bean);
}
