package wsg.tools.internet.base.data.support.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.base.data.support.Descriptors;

/**
 * Validates whether the values are valid {@link LocalDateTime}.
 *
 * @author Kingen
 * @since 2021/4/8
 */
public class LocalDateTimeValidator extends AbstractTimeValidator<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public LocalDateTimeValidator() {
        super(LocalDateTime.class);
        this.formatter = Constants.YYYY_MM_DD_HH_MM_SS;
    }

    public LocalDateTimeValidator(DateTimeFormatter formatter) {
        super(LocalDateTime.class);
        this.formatter = formatter;
    }

    @Override
    protected LocalDateTime parse(@Nonnull String text) throws DateTimeParseException {
        return LocalDateTime.parse(text, formatter);
    }

    @Override
    public void describe(List<LocalDateTime> values) {
        Descriptors.<LocalDateTime>range().describe(values);
    }
}
