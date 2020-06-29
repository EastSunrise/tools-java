package wsg.tools.common.jackson.deserializer.impl;

import wsg.tools.common.jackson.deserializer.base.AbstractStringDeserializer;
import wsg.tools.common.jackson.intf.AkaSerializable;
import wsg.tools.common.util.EnumUtils;

/**
 * Generic deserializer for {@link Enum} which are compared based on {@link AkaSerializable<String>#alsoKnownAs(String)}}
 *
 * @author Kingen
 * @since 2020/6/20
 */
public class EnumAkaStringDeserializer<T extends Enum<T> & AkaSerializable<String>> extends AbstractStringDeserializer<T> {

    private Class<T> clazz;

    private EnumAkaStringDeserializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <V extends Enum<V> & AkaSerializable<String>> EnumAkaStringDeserializer<V> of(Class<V> clazz) {
        return new EnumAkaStringDeserializer<>(clazz);
    }

    @Override
    public T toNonNullT(String s) {
        return EnumUtils.deserializeAka(s, clazz);
    }
}
