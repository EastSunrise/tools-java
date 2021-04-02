package wsg.tools.internet.base.data.support;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.data.InvalidValueException;

/**
 * Validates whether the values are valid durations.
 *
 * @author Kingen
 * @since 2021/3/31
 */
@Slf4j
public class DurationValidator extends AbstractValidator<Duration> {

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

    /**
     * Describes the range of durations.
     */
    @Override
    protected void describe(@Nonnull List<Duration> durations) {
        if (!durations.isEmpty()) {
            Duration max = durations.stream().max(Duration::compareTo).orElseThrow();
            Duration min = durations.stream().min(Duration::compareTo).orElseThrow();
            log.info("Min: {}", min);
            log.info("Max: {}", max);
        }
    }
}
