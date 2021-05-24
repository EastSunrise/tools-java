package wsg.tools.internet.common;

import wsg.tools.internet.base.EntityProperty;

/**
 * Represents a supplier of the title of an entity.
 *
 * @author Kingen
 * @since 2021/5/24
 */
@EntityProperty
public interface TitleSupplier {

    /**
     * Returns the title of the entity.
     *
     * @return the title
     */
    String getTitle();
}
