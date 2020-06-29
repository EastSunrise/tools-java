package wsg.tools.common.jackson.deserializer.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * Deserialize a property to an instance from {@link java.time} with {@link JsonFormat.Value}
 *
 * @author Kingen
 * @since 2020/6/22
 */
public abstract class AbstractLocalDateTimeDeserializer<T> extends AbstractStringDeserializer<T> implements ContextualDeserializer {

    private JsonFormat.Value format;
    private DateTimeFormatter standardFormatter;
    private DateTimeFormatter[] formatters;

    protected AbstractLocalDateTimeDeserializer(DateTimeFormatter standardFormatter, DateTimeFormatter... formatters) {
        this.standardFormatter = standardFormatter;
        this.formatters = formatters;
    }

    @Override
    public T toNonNullT(String text) {
        if (format != null && StringUtils.isNotBlank(format.getPattern())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.getPattern());
            try {
                return parse(text, withFormat(formatter));
            } catch (DateTimeParseException ignored) {
            }
        }

        if (formatters != null) {
            for (DateTimeFormatter formatter : formatters) {
                if (formatter != null) {
                    try {
                        return parse(text, withFormat(formatter));
                    } catch (DateTimeParseException ignored) {
                    }
                }
            }
        }

        return parse(text, withFormat(standardFormatter));
    }

    private DateTimeFormatter withFormat(DateTimeFormatter formatter) {
        if (format != null) {
            if (format.getLocale() != null) {
                formatter = formatter.withLocale(format.getLocale());
            }
            if (format.getTimeZone() != null) {
                formatter = formatter.withZone(format.getTimeZone().toZoneId());
            }
        }
        return formatter;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        format = context.getAnnotationIntrospector().findFormat(property.getMember());
        return this;
    }

    /**
     * Parse the text by a specified {@link DateTimeFormatter}
     *
     * @param text      the text to parse
     * @param formatter formatter to use
     * @return result
     */
    public abstract T parse(String text, DateTimeFormatter formatter);
}
