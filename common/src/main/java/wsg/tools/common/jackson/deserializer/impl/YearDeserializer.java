package wsg.tools.common.jackson.deserializer.impl;

import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.deserializer.base.AbstractLocalDateTimeDeserializer;

import java.time.Year;
import java.time.format.DateTimeFormatter;

/**
 * Deserialize to an instance of {@link Year} with formatters.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class YearDeserializer extends AbstractLocalDateTimeDeserializer<Year> {

    public YearDeserializer(DateTimeFormatter... formatters) {
        super(Constants.STANDARD_YEAR_FORMATTER, formatters);
    }

    @Override
    public Year parse(String text, DateTimeFormatter formatter) {
        return Year.parse(text, formatter);
    }
}
