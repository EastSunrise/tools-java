package wsg.tools.common.jackson.deserializer.impl;

import wsg.tools.common.jackson.deserializer.base.AbstractLocalDateTimeDeserializer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Deserialize to an instance of {@link LocalTime} with formatters.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class LocalTimeDeserializer extends AbstractLocalDateTimeDeserializer<LocalTime> {

    public LocalTimeDeserializer(DateTimeFormatter... formatters) {
        super(DateTimeFormatter.ISO_LOCAL_TIME, formatters);
    }

    @Override
    public LocalTime parse(String text, DateTimeFormatter formatter) {
        return LocalTime.parse(text, formatter);
    }
}
