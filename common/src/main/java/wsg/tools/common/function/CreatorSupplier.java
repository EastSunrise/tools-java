package wsg.tools.common.function;

/**
 * Supply an instance of the given type {@link T}.
 *
 * @author Kingen
 * @since 2020/7/23
 */
@FunctionalInterface
public interface CreatorSupplier<T> {

    /**
     * Create an instance of the type {@link T}.
     *
     * @return created instance
     */
    T create();
}
