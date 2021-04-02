package wsg.tools.internet.base.data.support;

import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.InvalidValueException;

/**
 * Validates the type of each element of values being validated.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public class TypeValidator<R> extends AbstractValidator<R> {

    private final Class<R> clazz;

    public TypeValidator(Class<R> clazz) {
        this.clazz = clazz;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public R convert(@Nonnull Object value) throws InvalidValueException {
        if (clazz.isInstance(value)) {
            return (R) value;
        }
        throw new InvalidValueException("Not a instance of " + clazz);
    }

    @Override
    protected void describe(@Nonnull List<R> values) {

    }
}
