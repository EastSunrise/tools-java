package wsg.tools.internet.base.view;

import wsg.tools.internet.base.EntityProperty;

/**
 * Indicates that the entity has an integer identifier.
 * <p>
 * Note that this interface is conflict with {@link Identifier} or {@link LongIdentifier}.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@EntityProperty
public interface IntIdentifier {

    /**
     * Returns the integer identifier
     *
     * @return the identifier
     */
    int getId();
}
