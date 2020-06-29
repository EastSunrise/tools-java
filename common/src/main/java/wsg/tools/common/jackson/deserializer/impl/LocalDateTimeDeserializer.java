package wsg.tools.common.jackson.deserializer.impl;

import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.deserializer.base.AbstractLocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Deserialize to an instance of {@link LocalDateTime} with formatters.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class LocalDateTimeDeserializer extends AbstractLocalDateTimeDeserializer<LocalDateTime> {

    public LocalDateTimeDeserializer(DateTimeFormatter... formatters) {
        super(Constants.STANDARD_DATE_TIME_FORMATTER, formatters);
    }

    @Override
    public LocalDateTime parse(String text, DateTimeFormatter formatter) {
        return LocalDateTime.parse(text, formatter);
    }
}
