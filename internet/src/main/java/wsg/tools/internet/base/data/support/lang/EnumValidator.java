package wsg.tools.internet.base.data.support.lang;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.data.Validator;
import wsg.tools.internet.base.data.support.Descriptors;
import wsg.tools.internet.base.data.support.InvalidValueException;

/**
 * Validates whether the values are enumerable.
 *
 * @author Kingen
 * @since 2021/4/1
 */
@Slf4j
public class EnumValidator extends Validator<Object> {

    @Nonnull
    @Override
    public Object convert(@Nonnull Object value) throws InvalidValueException {
        return value;
    }

    @Override
    public void describe(@Nonnull List<Object> values) {
        Descriptors.enumerate(Function.identity()).describe(values);
    }
}
