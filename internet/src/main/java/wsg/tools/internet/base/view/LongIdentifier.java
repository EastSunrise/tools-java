package wsg.tools.internet.base.view;

import wsg.tools.internet.base.EntityProperty;

/**
 * Indicates that the entity has an long identifier.
 * <p>
 * Note that this interface is conflict with {@link Identifier} or {@link IntIdentifier}.
 *
 * @author Kingen
 * @since 2021/5/16
 */
@EntityProperty
public interface LongIdentifier {

    /**
     * Returns the integer identifier
     *
     * @return the identifier
     */
    long getId();
}
