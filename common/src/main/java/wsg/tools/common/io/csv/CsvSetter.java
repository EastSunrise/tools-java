package wsg.tools.common.io.csv;

import wsg.tools.common.util.function.Setter;

/**
 * A function to set a string to a bean.
 *
 * @param <T> type of the bean to which the string is set
 * @author Kingen
 * @since 2021/3/31
 */
public interface CsvSetter<T> extends Setter<T, String> {

    /**
     * Sets a string to the given bean.
     *
     * @param bean  the bean to which the value is set
     * @param value the value to be set
     */
    @Override
    void set(T bean, String value);
}
