package wsg.tools.common.util.function.throwable;

/**
 * Represents a supplier of results, which may throw an exception.
 *
 * @author Kingen
 * @since 2020/11/4
 */
@FunctionalInterface
public interface ThrowableSupplier<R, E extends Throwable> {

    /**
     * Obtains a result.
     *
     * @return a result
     * @throws E thrown exception
     */
    R get() throws E;
}
