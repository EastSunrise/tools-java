package wsg.tools.internet.base.data.support.lang;

import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.support.AbstractParsableValidator;
import wsg.tools.internet.base.data.support.Descriptors;
import wsg.tools.internet.base.data.support.InvalidValueException;

/**
 * Validates whether the values are valid numbers of the specified type.
 *
 * @param <N> type of numbers
 * @author Kingen
 * @since 2021/4/3
 */
abstract class AbstractNumberValidator<N extends Number & Comparable<N>>
    extends AbstractParsableValidator<N> {

    protected AbstractNumberValidator(Class<N> clazz) {
        super(clazz);
    }

    @Override
    protected N parseText(@Nonnull String text) throws InvalidValueException {
        try {
            return this.parseValue(text);
        } catch (NumberFormatException e) {
            throw new InvalidValueException(e.getMessage());
        }
    }

    /**
     * Parses the specified string as a number.
     *
     * @param text string to be parsed
     * @return the specified value
     */
    protected abstract N parseValue(@Nonnull String text);

    @Override
    public void describe(List<N> values) {
        Descriptors.range(values);
    }
}
