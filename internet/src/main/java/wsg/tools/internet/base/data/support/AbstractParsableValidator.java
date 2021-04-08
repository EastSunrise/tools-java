package wsg.tools.internet.base.data.support;

import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.Validator;

/**
 * This class provides a skeletal implementation to validate the values that can be parsed from
 * strings.
 *
 * @author Kingen
 * @since 2021/4/8
 */
public abstract class AbstractParsableValidator<R> extends Validator<R> {

    private final Class<R> clazz;

    protected AbstractParsableValidator(Class<R> clazz) {
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R convert(@Nonnull Object value) throws InvalidValueException {
        if (clazz.isInstance(value)) {
            return (R) value;
        }
        return parseText(value.toString());
    }

    /**
     * Parses the specified string as a target object.
     *
     * @param text string to be parsed
     * @return the specified value
     * @throws InvalidValueException if the text is invalid
     */
    protected abstract R parseText(@Nonnull String text) throws InvalidValueException;
}
