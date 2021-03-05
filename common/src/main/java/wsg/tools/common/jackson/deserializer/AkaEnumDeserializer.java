package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.AkaPredicate;

/**
 * Deserialize a value to an enum that implements {@link AkaPredicate}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public class AkaEnumDeserializer<A, E extends Enum<E> & AkaPredicate<A>> extends
    AbstractEnumDeserializer<A, E> {

    public AkaEnumDeserializer(Class<A> aClass, Class<E> eClass) {
        super(aClass, eClass);
    }

    @Override
    public E deserialize(Class<E> eClass, A value) {
        return EnumUtilExt.deserializeAka(value, eClass);
    }
}
