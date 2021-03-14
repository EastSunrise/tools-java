package wsg.tools.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.apache.commons.lang3.StringUtils;

/**
 * Deserialize timestamp to {@link LocalDateTime}.
 *
 * @author Kingen
 * @since 2020/12/24
 */
public class TimestampDeserializer extends StdDeserializer<LocalDateTime> {

    private final ZoneId zoneId;

    protected TimestampDeserializer() {
        this(ZoneId.systemDefault());
    }

    protected TimestampDeserializer(ZoneId zoneId) {
        super(LocalDateTime.class);
        this.zoneId = zoneId;
    }

    public static TimestampDeserializer getInstance() {
        return new TimestampDeserializer(ZoneId.systemDefault());
    }

    public static TimestampDeserializer getInstance(ZoneId zoneId) {
        return new TimestampDeserializer(zoneId);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext context)
        throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return null;
        }
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return Instant.ofEpochSecond(p.getLongValue()).atZone(zoneId).toLocalDateTime();
        }
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String text = p.getText();
            if (context.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                && StringUtils.isBlank(text)) {
                return null;
            }
            try {
                return Instant.ofEpochSecond(Long.parseLong(text)).atZone(zoneId).toLocalDateTime();
            } catch (NumberFormatException ignored) {
            }
        }
        return (LocalDateTime) context.handleUnexpectedToken(LocalDateTime.class, p);
    }
}
