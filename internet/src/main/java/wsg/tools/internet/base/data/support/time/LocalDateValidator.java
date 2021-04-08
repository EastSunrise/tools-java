package wsg.tools.internet.base.data.support.time;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.support.Descriptors;

/**
 * Validates whether the values are valid {@link LocalDate}.
 *
 * @author Kingen
 * @since 2021/4/8
 */
public class LocalDateValidator extends AbstractTimeValidator<LocalDate> {

    private final DateTimeFormatter formatter;

    public LocalDateValidator() {
        super(LocalDate.class);
        this.formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    }

    public LocalDateValidator(DateTimeFormatter formatter) {
        super(LocalDate.class);
        this.formatter = formatter;
    }

    @Override
    public void describe(List<LocalDate> values) {
        Descriptors.<LocalDate>range().describe(values);
    }

    @Override
    protected LocalDate parse(@Nonnull String text) throws DateTimeParseException {
        return LocalDate.parse(text, formatter);
    }
}
