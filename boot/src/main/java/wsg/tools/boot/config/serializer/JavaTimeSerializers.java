package wsg.tools.boot.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Duration;

/**
 * Converters for {@link java.time}.
 *
 * @author Kingen
 * @since 2020/7/21
 */
public final class JavaTimeSerializers {

    public static abstract class BaseDurationToLongSerializer extends StdSerializer<Duration> {

        public static final BaseDurationToLongSerializer DURATION_TO_DAYS_CONVERTER = new BaseDurationToLongSerializer() {
            @Override
            public long convert(Duration duration) {
                return duration.toDays();
            }
        };

        public static final BaseDurationToLongSerializer DURATION_TO_HOURS_CONVERTER = new BaseDurationToLongSerializer() {
            @Override
            public long convert(Duration duration) {
                return duration.toHours();
            }
        };

        public static final BaseDurationToLongSerializer DURATION_TO_MINUTES_CONVERTER = new BaseDurationToLongSerializer() {
            @Override
            public long convert(Duration duration) {
                return duration.toMinutes();
            }
        };

        public static final BaseDurationToLongSerializer DURATION_TO_SECONDS_CONVERTER = new BaseDurationToLongSerializer() {
            @Override
            public long convert(Duration duration) {
                return duration.toSeconds();
            }
        };

        public static final BaseDurationToLongSerializer DURATION_TO_MILLIS_CONVERTER = new BaseDurationToLongSerializer() {
            @Override
            public long convert(Duration duration) {
                return duration.toMillis();
            }
        };

        public static final BaseDurationToLongSerializer DURATION_TO_NANOS_CONVERTER = new BaseDurationToLongSerializer() {
            @Override
            public long convert(Duration duration) {
                return duration.toNanos();
            }
        };

        protected BaseDurationToLongSerializer() {
            super(Duration.class);
        }

        @Override
        public void serialize(Duration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeNumber(convert(value));
            }
        }

        /**
         * Convert a {@link Duration} to long
         *
         * @param value duration to convert
         * @return result of long
         */
        protected abstract long convert(Duration value);
    }
}
