package wsg.tools.internet.base.data.support.time;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.Validator;
import wsg.tools.internet.base.data.support.Descriptors;
import wsg.tools.internet.base.data.support.InvalidValueException;

/**
 * Validates whether the values are valid durations.
 *
 * @author Kingen
 * @since 2021/3/31
 */
public class DurationValidator extends Validator<Duration> {

    @Nonnull
    @Override
    public Duration convert(@Nonnull Object value) throws InvalidValueException {
        if (value instanceof Duration) {
            return (Duration) value;
        }
        try {
            return Duration.parse(value.toString());
        } catch (DateTimeParseException e) {
            throw new InvalidValueException(e.getMessage());
        }
    }

    @Override
    public void describe(List<Duration> values) {
        Descriptors.<Duration>range().describe(values);
    }
}
