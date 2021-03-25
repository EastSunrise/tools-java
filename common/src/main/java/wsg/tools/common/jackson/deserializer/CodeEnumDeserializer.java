package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.IntCodeSupplier;

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

    public static <E extends Enum<E> & IntCodeSupplier> CodeEnumDeserializer<Integer, E>
    intCodeEnum(Class<E> eClass) {
        return new CodeEnumDeserializer<>(Integer.class, eClass);
    }

    @Override
    public E deserialize(Class<E> eClass, C value) {
        return EnumUtilExt.valueOfCode(value, eClass);
    }
}
