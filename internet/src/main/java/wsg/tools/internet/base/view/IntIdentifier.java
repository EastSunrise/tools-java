package wsg.tools.internet.base.view;

import wsg.tools.internet.base.EntityInterface;

/**
 * Indicates that the entity has an integer identifier.
 * <p>
 * Note that this interface is conflict with {@link Identifier}.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@EntityInterface
public interface IntIdentifier {

    /**
     * Returns the integer identifier
     *
     * @return the identifier
     */
    int getId();
}
