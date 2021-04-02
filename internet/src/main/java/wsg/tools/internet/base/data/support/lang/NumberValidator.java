package wsg.tools.internet.base.data.support.lang;

import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.Validator;
import wsg.tools.internet.base.data.support.Descriptors;
import wsg.tools.internet.base.data.support.InvalidValueException;

/**
 * Validates whether the values are valid numbers of the specified type.
 *
 * @param <N> type of numbers
 * @author Kingen
 * @since 2021/4/3
 */
public abstract class NumberValidator<N extends Number & Comparable<N>> extends Validator<N> {

    @Override
    public N convert(@Nonnull Object value) throws InvalidValueException {
        if (value instanceof Number) {
            return value(((Number) value));
        }
        try {
            return parseValue(value.toString());
        } catch (NumberFormatException e) {
            throw new InvalidValueException(e.getMessage());
        }
    }

    /**
     * Returns the specified value of the number.
     *
     * @param number number to be convert
     * @return the specified value
     */
    protected abstract N value(@Nonnull Number number);

    /**
     * Parses the specified string as a number.
     *
     * @param text string to be parsed
     * @return the specified value
     */
    protected abstract N parseValue(@Nonnull String text);

    @Override
    public void describe(List<N> values) {
        Descriptors.<N>range().describe(values);
    }
}
