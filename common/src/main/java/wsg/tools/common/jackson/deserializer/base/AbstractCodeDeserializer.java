package wsg.tools.common.jackson.deserializer.base;

import wsg.tools.common.jackson.intf.CodeSerializable;

/**
 * Deserialize a property to an instance of object implementing {@link CodeSerializable<C>}
 *
 * @author Kingen
 * @since 2020/6/27
 */
public abstract class AbstractCodeDeserializer<C, T extends CodeSerializable<C>> extends AbstractNonNullDeserializer<C, T> {
    public AbstractCodeDeserializer(Class<C> cClass) {
        super(cClass);
    }
}
