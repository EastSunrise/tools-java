package wsg.tools.internet.base;

/**
 * Indicates that the entity has an identifier.
 *
 * @param <ID> type of the identifiers
 * @author Kingen
 * @since 2021/1/9
 */
@EntityInterface
public interface Identifier<ID> {

    /**
     * Returns the identifier
     *
     * @return id
     */
    ID getId();
}
