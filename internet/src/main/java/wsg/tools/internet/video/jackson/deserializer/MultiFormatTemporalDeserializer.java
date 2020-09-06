package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.AbstractStringDeserializer;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Locale;

/**
 * Deserialize string to {@link LocalDate} with different formats.
 *
 * @author Kingen
 * @since 2020/9/1
 */
public class MultiFormatTemporalDeserializer extends AbstractStringDeserializer<Temporal> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMMM yyyy").withLocale(Locale.ENGLISH);
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(Locale.ENGLISH);

    protected MultiFormatTemporalDeserializer() {
        super(Temporal.class);
    }

    @Override
    protected Temporal parseText(String text) {
        try {
            return LocalDate.parse(text, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            try {
                return YearMonth.parse(text, MONTH_FORMAT);
            } catch (DateTimeParseException exception) {
                return Year.of(Integer.parseInt(text));
            }
        }
    }
}
