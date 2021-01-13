package wsg.tools.common.lang;

/**
 * Supply an identifier for the object.
 *
 * @author Kingen
 * @since 2021/1/9
 */
@FunctionalInterface
public interface Identifier<T> {

    /**
     * Obtains the identifier
     *
     * @return id
     */
    T getId();
}
