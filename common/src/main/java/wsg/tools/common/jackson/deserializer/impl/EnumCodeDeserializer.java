package wsg.tools.common.jackson.deserializer.impl;

import wsg.tools.common.jackson.deserializer.base.AbstractNonNullDeserializer;
import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.common.util.EnumUtilExt;

/**
 * Generic deserializer for {@link Enum} which are compared based on {@link CodeSerializable#getCode()}
 *
 * @author Kingen
 * @since 2020/6/19
 */
public class EnumCodeDeserializer<C, E extends Enum<E> & CodeSerializable<C>> extends AbstractNonNullDeserializer<C, E> {

    private Class<E> eClass;

    public EnumCodeDeserializer(Class<C> cClass, Class<E> eClass) {
        super(cClass);
        this.eClass = eClass;
    }

    @Override
    public E toNonNullT(C c) {
        return EnumUtilExt.deserializeCode(c, eClass);
    }
}
