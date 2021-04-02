package wsg.tools.internet.base.data.support.util;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.Validator;
import wsg.tools.internet.base.data.support.Descriptors;
import wsg.tools.internet.base.data.support.InvalidValueException;

/**
 * Validates the type of each element of values.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public class TypeValidator extends Validator<Class<?>> {

    @Nonnull
    @Override
    public Class<?> convert(@Nonnull Object value) throws InvalidValueException {
        return value.getClass();
    }

    @Override
    public void describe(@Nonnull List<Class<?>> classes) {
        Descriptors.<Class<?>, Class<?>>enumerate(Function.identity()).describe(classes);
    }
}
