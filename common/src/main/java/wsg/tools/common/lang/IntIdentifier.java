package wsg.tools.common.lang;

/**
 * Supply an integer identifier for the object.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@FunctionalInterface
public interface IntIdentifier {

    /**
     * Obtains the identifier
     *
     * @return id
     */
    int getId();
}
