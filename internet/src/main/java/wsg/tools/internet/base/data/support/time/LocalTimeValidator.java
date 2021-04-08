package wsg.tools.internet.base.data.support.time;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.support.Descriptors;

/**
 * Validates whether the values are valid {@link LocalTime}.
 *
 * @author Kingen
 * @since 2021/4/8
 */
public class LocalTimeValidator extends AbstractTimeValidator<LocalTime> {

    private final DateTimeFormatter formatter;

    public LocalTimeValidator() {
        super(LocalTime.class);
        this.formatter = DateTimeFormatter.ISO_LOCAL_TIME;
    }

    public LocalTimeValidator(DateTimeFormatter formatter) {
        super(LocalTime.class);
        this.formatter = formatter;
    }

    @Override
    public void describe(List<LocalTime> values) {
        Descriptors.<LocalTime>range().describe(values);
    }

    @Override
    protected LocalTime parse(@Nonnull String text) throws DateTimeParseException {
        return LocalTime.parse(text, formatter);
    }
}
