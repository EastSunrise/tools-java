package wsg.tools.common.util.function;

import java.util.Map;

/**
 * Represents a supplier of an instance of the given type {@link T}.
 *
 * @author Kingen
 * @since 2020/7/23
 */
@FunctionalInterface
public interface CreatorSupplier<T> {

    /**
     * Create an instance of the type {@link T}.
     *
     * @param map map of properties
     * @return created instance
     */
    T create(Map<String, Object> map);
}
