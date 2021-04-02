package wsg.tools.common.util.function;

/**
 * Represents a supplier of codes.
 *
 * @param <C> the type of supplied codes
 * @author Kingen
 * @since 2020/6/17
 */
@FunctionalInterface
public interface CodeSupplier<C> {

    /**
     * Returns a code.
     *
     * @return a code
     */
    C getCode();
}
