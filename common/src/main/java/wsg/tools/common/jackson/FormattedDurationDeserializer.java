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
        if (JsonDurationFormat.Format.LONG == format) {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return ofLong(p.getLongValue(), ctxt);
            }
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                return ofLong(Long.parseLong(p.getText()), ctxt);
            }
        }
        if (JsonDurationFormat.Format.DOUBLE == format) {
            if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
                return DecimalUtils
                    .extractSecondsAndNanos(p.getDecimalValue(), Duration::ofSeconds);
            }
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                return DecimalUtils
                    .extractSecondsAndNanos(new BigDecimal(p.getText()), Duration::ofSeconds);
            }
        }
        if (JsonDurationFormat.Format.DURATION == format) {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                return TimeUtils.parseDuration(p.getText().trim());
            }
        }
        if (JsonDurationFormat.Format.DEFAULT == format) {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                return Duration.parse(p.getText().trim());
            }
        }
        return (Duration) ctxt.handleUnexpectedToken(Duration.class, p);
    }

    private Duration ofLong(long value, DeserializationContext ctxt) {
        if (ctxt.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            return Duration.ofSeconds(value);
        }
        return Duration.ofMillis(value);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
        BeanProperty property) {
        JsonDurationFormat jdf = property.getAnnotation(JsonDurationFormat.class);
        if (null == jdf) {
            return new FormattedDurationDeserializer();
        }
        return new FormattedDurationDeserializer(jdf.format());
    }
}
