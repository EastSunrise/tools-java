package wsg.tools.common.util.function;

/**
 * Supply a code, usually stored in the database.
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
