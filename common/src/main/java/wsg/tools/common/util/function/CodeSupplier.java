package wsg.tools.common.util.function;

/**
 * Represents a supplier of a code.
 *
 * @author Kingen
 * @since 2020/6/17
 */
@FunctionalInterface
public interface CodeSupplier {

    /**
     * Returns the code.
     *
     * @return the code
     */
    String getCode();
}
