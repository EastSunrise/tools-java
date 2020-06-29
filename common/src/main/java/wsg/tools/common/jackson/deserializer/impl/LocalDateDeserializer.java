package wsg.tools.common.jackson.deserializer.impl;

import wsg.tools.common.jackson.deserializer.base.AbstractLocalDateTimeDeserializer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Deserialize to an instance of {@link LocalDate} with formatters.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class LocalDateDeserializer extends AbstractLocalDateTimeDeserializer<LocalDate> {

    public LocalDateDeserializer(DateTimeFormatter... formatters) {
        super(DateTimeFormatter.ISO_LOCAL_DATE, formatters);
    }

    @Override
    public LocalDate parse(String text, DateTimeFormatter formatter) {
        return LocalDate.parse(text, formatter);
    }
}
