package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * Deserialize a title to an enum that implements {@link TitleSupplier}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public class TitleEnumDeserializer<E extends Enum<E> & TitleSupplier> extends
    AbstractEnumDeserializer<String, E> {

    private final boolean ignoreCase;

    public TitleEnumDeserializer(Class<E> eClass, Boolean ignoreCase) {
        super(String.class, eClass);
        this.ignoreCase = ignoreCase;
    }

    public TitleEnumDeserializer(Class<E> eClass) {
        super(String.class, eClass);
        this.ignoreCase = false;
    }

    @Override
    public E deserialize(Class<E> eClass, String value) {
        return EnumUtilExt.deserializeTitle(value, eClass, ignoreCase);
    }
}
