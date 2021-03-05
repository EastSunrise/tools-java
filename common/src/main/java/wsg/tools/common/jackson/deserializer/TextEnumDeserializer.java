package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.TextSupplier;

/**
 * Deserialize a text to an enum that implements {@link TextSupplier}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public class TextEnumDeserializer<E extends Enum<E> & TextSupplier> extends
    AbstractEnumDeserializer<String, E> {

    private final boolean ignoreCase;

    public TextEnumDeserializer(Class<E> eClass, boolean ignoreCase) {
        super(String.class, eClass);
        this.ignoreCase = ignoreCase;
    }

    public TextEnumDeserializer(Class<E> eClass) {
        super(String.class, eClass);
        this.ignoreCase = false;
    }

    @Override
    public E deserialize(Class<E> eClass, String value) {
        return EnumUtilExt.deserializeText(value, eClass, ignoreCase);
    }
}
