package wsg.tools.internet.base.data;

import javax.annotation.Nonnull;

/**
 * Represents a function that converts an object to the target type.
 *
 * @param <R> target type that the value is converted to
 * @author Kingen
 * @since 2021/4/2
 */
public interface Converter<R> {

    /**
     * Converts the value to target type.
     *
     * @param value the value to be converted, must not be null
     * @return converted value of target type
     * @throws InvalidValueException if the given value is invalid
     */
    R convert(@Nonnull Object value) throws InvalidValueException;
}
