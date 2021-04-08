package wsg.tools.internet.base.data.support.time;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.support.Descriptors;

/**
 * Validates whether the values are valid durations.
 *
 * @author Kingen
 * @since 2021/3/31
 */
public class DurationValidator extends AbstractTimeValidator<Duration> {

    public DurationValidator() {
        super(Duration.class);
    }

    @Override
    public void describe(List<Duration> values) {
        Descriptors.<Duration>range().describe(values);
    }

    @Override
    protected Duration parse(@Nonnull String text) throws DateTimeParseException {
        return Duration.parse(text);
    }
}
