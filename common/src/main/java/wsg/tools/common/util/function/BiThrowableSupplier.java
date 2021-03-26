package wsg.tools.common.util.function;

/**
 * Represents a supplier that returns a result and throw two exceptions. This is an extension of
 * {@link org.apache.commons.lang3.Functions.FailableSupplier}.
 *
 * @author Kingen
 * @since 2021/3/26
 */
public interface BiThrowableSupplier<R, E extends Throwable, F extends Throwable> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws E the first thrown exception
     * @throws F the second thrown exception
     */
    R get() throws E, F;
}
