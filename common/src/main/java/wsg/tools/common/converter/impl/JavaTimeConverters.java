package wsg.tools.common.converter.impl;

import wsg.tools.common.converter.base.BaseConverter;

import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Converters for {@link java.time}.
 *
 * @author Kingen
 * @since 2020/7/21
 */
public class JavaTimeConverters {

    public static Collection<BaseConverter<?, ?>> getConverters() {
        List<BaseConverter<?, ?>> converters = new ArrayList<>();
        converters.add(YearToIntConverter.INSTANCE);
        return converters;
    }

    public static class YearToIntConverter extends BaseConverter<Year, Integer> {
        public static final YearToIntConverter INSTANCE = new YearToIntConverter();

        protected YearToIntConverter() {
            super(Year.class, Integer.class);
        }

        @Override
        public Integer convert(Year year) {
            return year.getValue();
        }
    }

    public static abstract class DurationToLongConverter extends BaseConverter<Duration, Long> {

        public static final DurationToLongConverter DURATION_TO_DAYS_CONVERTER = new DurationToLongConverter() {
            @Override
            public Long convert(Duration duration) {
                return duration.toDays();
            }
        };

        public static final DurationToLongConverter DURATION_TO_HOURS_CONVERTER = new DurationToLongConverter() {
            @Override
            public Long convert(Duration duration) {
                return duration.toHours();
            }
        };

        public static final DurationToLongConverter DURATION_TO_MINUTES_CONVERTER = new DurationToLongConverter() {
            @Override
            public Long convert(Duration duration) {
                return duration.toMinutes();
            }
        };

        public static final DurationToLongConverter DURATION_TO_SECONDS_CONVERTER = new DurationToLongConverter() {
            @Override
            public Long convert(Duration duration) {
                return duration.toSeconds();
            }
        };

        public static final DurationToLongConverter DURATION_TO_MILLIS_CONVERTER = new DurationToLongConverter() {
            @Override
            public Long convert(Duration duration) {
                return duration.toMillis();
            }
        };

        public static final DurationToLongConverter DURATION_TO_NANOS_CONVERTER = new DurationToLongConverter() {
            @Override
            public Long convert(Duration duration) {
                return duration.toNanos();
            }
        };

        protected DurationToLongConverter() {
            super(Duration.class, Long.class);
        }
    }
}
