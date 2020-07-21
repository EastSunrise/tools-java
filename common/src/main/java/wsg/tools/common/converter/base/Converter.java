package wsg.tools.common.converter.base;

import com.sun.istack.NotNull;

/**
 * A function to convert an object to another object.
 *
 * @author Kingen
 * @since 2020/7/21
 */
@FunctionalInterface
public interface Converter<S, T> {

    /**
     * Convert a {@link S} to a {@link T}.
     *
     * @param s source object
     * @return target object
     */
    T convert(@NotNull S s);
}
