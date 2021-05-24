package wsg.tools.common.util.function;

/**
 * Represents a supplier of the alias of an object.
 *
 * @author Kingen
 * @since 2021/5/21
 */
@FunctionalInterface
public interface AliasSupplier {

    /**
     * Returns the alias.
     *
     * @return the alias
     */
    String[] getAlias();
}
