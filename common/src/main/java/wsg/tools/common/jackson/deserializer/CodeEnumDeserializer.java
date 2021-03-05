package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.CodeSupplier;

/**
 * Deserialize a code to an enum that implements {@link CodeSupplier}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public class CodeEnumDeserializer<C, E extends Enum<E> & CodeSupplier<C>> extends
    AbstractEnumDeserializer<C, E> {

    public CodeEnumDeserializer(Class<C> cClass, Class<E> eClass) {
        super(cClass, eClass);
    }

    @Override
    public E deserialize(Class<E> eClass, C value) {
        return EnumUtilExt.deserializeCode(value, eClass);
    }
}
