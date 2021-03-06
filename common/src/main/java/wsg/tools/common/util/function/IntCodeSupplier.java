package wsg.tools.common.util.function;

/**
 * Represents a supplier of an integer code.
 *
 * @author Kingen
 * @since 2021/2/21
 */
@FunctionalInterface
public interface IntCodeSupplier {

    /**
     * Returns the code.
     *
     * @return the code
     */
    int getCode();
}
