package wsg.tools.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;
import wsg.tools.common.time.TimeUtils;

/**
 * The deserializer that deserializes a token to a {@link Duration} in some formats.
 *
 * @author Kingen
 * @see JsonDurationFormat
 * @since 2021/6/5
 */
public class FormattedDurationDeserializer extends StdScalarDeserializer<Duration>
    implements ContextualDeserializer {

    private final JsonDurationFormat.Format format;

    public FormattedDurationDeserializer() {
        this(JsonDurationFormat.Format.DEFAULT);
    }

    protected FormattedDurationDeserializer(JsonDurationFormat.Format format) {
        super(Duration.class);
        this.format = Objects.requireNonNull(format);
    }

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (format == JsonDurationFormat.Format.LONG) {
            long value;
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                value = p.getLongValue();
            } else if (p.hasToken(JsonToken.VALUE_STRING)) {
                value = Long.parseLong(p.getText());
            } else {
                return (Duration) ctxt.handleUnexpectedToken(Duration.class, p);
            }
            if (ctxt.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                return Duration.ofSeconds(value);
            }
            return Duration.ofMillis(value);
        }
        if (format == JsonDurationFormat.Format.DOUBLE) {
            BigDecimal bigDecimal;
            if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
                bigDecimal = p.getDecimalValue();
            } else if (p.hasToken(JsonToken.VALUE_STRING)) {
                bigDecimal = new BigDecimal(p.getText());
            } else {
                return (Duration) ctxt.handleUnexpectedToken(Duration.class, p);
            }
            return DecimalUtils.extractSecondsAndNanos(bigDecimal, Duration::ofSeconds);
        }
        if (format == JsonDurationFormat.Format.DURATION) {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                return TimeUtils.parseDuration(p.getText().trim());
            }
        }

        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return Duration.parse(p.getText().trim());
        }
        return (Duration) ctxt.handleUnexpectedToken(Duration.class, p);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty prop) {
        JsonDurationFormat jdf = prop.getAnnotation(JsonDurationFormat.class);
        if (jdf == null) {
            return new FormattedDurationDeserializer();
        }
        return new FormattedDurationDeserializer(jdf.format());
    }
}
