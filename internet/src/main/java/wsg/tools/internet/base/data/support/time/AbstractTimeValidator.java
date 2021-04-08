package wsg.tools.internet.base.data.support.time;

import java.time.format.DateTimeParseException;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.support.AbstractParsableValidator;
import wsg.tools.internet.base.data.support.InvalidValueException;

/**
 * This class is a skeletal implementation that validates {@link java.time} objects.
 *
 * @author Kingen
 * @since 2021/4/8
 */
abstract class AbstractTimeValidator<R> extends AbstractParsableValidator<R> {

    protected AbstractTimeValidator(Class<R> clazz) {
        super(clazz);
    }

    @Override
    protected R parseText(@Nonnull String text) throws InvalidValueException {
        try {
            return parse(text);
        } catch (DateTimeParseException e) {
            throw new InvalidValueException(e.getMessage());
        }
    }

    /**
     * Parses the text to a {@link java.time} object.
     *
     * @param text the text to be parsed
     * @return a {@link java.time} object
     * @throws DateTimeParseException if the text cannot be parsed
     */
    protected abstract R parse(@Nonnull String text) throws DateTimeParseException;
}
