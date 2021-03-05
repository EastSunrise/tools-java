package wsg.tools.internet.common;

import javax.annotation.Nonnull;

/**
 * Supplies the first identifier of an iterable repository.
 *
 * @author Kingen
 * @since 2021/3/2
 */
@FunctionalInterface
public interface FirstSupplier<T> {

    /**
     * Returns the first identifier of an iterable repository
     *
     * @return the first identifier
     */
    @Nonnull
    T first();
}
