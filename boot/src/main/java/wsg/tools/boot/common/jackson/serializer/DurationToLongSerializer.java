package wsg.tools.boot.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;

/**
 * Serialize a {@link Duration} to long.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public final class DurationToLongSerializer extends StdSerializer<Duration> {

    private final Function<Duration, Long> converter;

    public DurationToLongSerializer(Function<Duration, Long> converter) {
        super(Duration.class);
        this.converter = converter;
    }

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
        if (null == value) {
            gen.writeNull();
        } else {
            gen.writeNumber(converter.apply(value));
        }
    }
}
