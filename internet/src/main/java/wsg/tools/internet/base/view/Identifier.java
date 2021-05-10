package wsg.tools.internet.base.view;

import wsg.tools.internet.base.EntityProperty;

/**
 * Indicates that the entity has an identifier.
 *
 * @param <ID> type of the identifiers
 * @author Kingen
 * @since 2021/1/9
 */
@EntityProperty
public interface Identifier<ID> {

    /**
     * Returns the identifier
     *
     * @return id
     */
    ID getId();
}
