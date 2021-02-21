package wsg.tools.common.util.function;

/**
 * Represents a supplier of a code.
 *
 * @author Kingen
 * @since 2020/6/17
 */
@FunctionalInterface
public interface CodeSupplier<Code> {
    /**
     * Serialize to a code
     *
     * @return code serialized
     */
    Code getCode();
}
