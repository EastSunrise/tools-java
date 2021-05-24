package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.AliasSupplier;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Provides common deserializers that deserializes an enum.
 *
 * @author Kingen
 * @since 2021/5/24
 */
public final class EnumDeserializers {

    private EnumDeserializers() {
    }

    public static <E extends Enum<E> & CodeSupplier> AbstractEnumDeserializer<String, E>
    ofCode(Class<E> enumClass) {
        return new AbstractEnumDeserializer<>(String.class, enumClass) {
            @Override
            public E valueOf(Class<E> eClass, String value) {
                return EnumUtilExt.valueOfCode(eClass, value);
            }
        };
    }

    public static <E extends Enum<E> & IntCodeSupplier> AbstractEnumDeserializer<Integer, E>
    ofIntCode(Class<E> enumClass) {
        return new AbstractEnumDeserializer<>(Integer.class, enumClass) {
            @Override
            public E valueOf(Class<E> eClass, Integer value) {
                return EnumUtilExt.valueOfIntCode(eClass, value);
            }
        };
    }

    public static <E extends Enum<E> & AliasSupplier> AbstractEnumDeserializer<String, E>
    ofAlias(Class<E> enumClass) {
        return new AbstractEnumDeserializer<>(String.class, enumClass) {
            @Override
            public E valueOf(Class<E> eClass, String value) {
                return EnumUtilExt.valueOfAlias(eClass, value);
            }
        };
    }
}
