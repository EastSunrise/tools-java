package wsg.tools.boot.config.serializer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import wsg.tools.common.jackson.intf.TitleSerializable;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Convert an object to a string when exporting and importing csv files.
 *
 * @author Kingen
 * @since 2020/7/21
 */
public class CsvConverters {

    private static final Map<Class<?>, Converter<?, String>> CONVERTERS = new ConcurrentHashMap<>(16);

    static {
        CONVERTERS.put(List.class, ListToStringConverter.INSTANCE);
        CONVERTERS.put(Duration.class, DurationMinuteConverter.INSTANCE);
        CONVERTERS.put(TitleSerializable.class, TitleConverter.INSTANCE);
    }

    public static String convert(Object value) {
        if (value == null) {
            return null;
        }
        Converter<Object, String> converter = get(value.getClass());
        if (converter != null) {
            return converter.convert(value);
        }
        return value.toString();
    }

    @SuppressWarnings("unchecked")
    private static Converter<Object, String> get(Class<?> clazz) {
        if (clazz == null || Object.class == clazz) {
            return null;
        }
        Converter<Object, String> converter = (Converter<Object, String>) CONVERTERS.get(clazz);
        if (converter != null) {
            return converter;
        }
        converter = get(clazz.getSuperclass());
        if (converter != null) {
            return converter;
        }
        for (Class<?> type : clazz.getInterfaces()) {
            converter = get(type);
            if (converter != null) {
                return converter;
            }
        }
        return null;
    }

    public static class ListToStringConverter implements Converter<List<?>, String> {

        public static final ListToStringConverter INSTANCE = new ListToStringConverter();

        private static final String SEPARATOR = "/";

        @Override
        public String convert(List<?> values) {
            List<String> strings = values.stream().map(CsvConverters::convert).collect(Collectors.toList());
            return StringUtils.join(strings, SEPARATOR);
        }
    }

    public static class DurationMinuteConverter implements Converter<Duration, String> {

        public static final DurationMinuteConverter INSTANCE = new DurationMinuteConverter();

        protected DurationMinuteConverter() {
        }

        @Override
        public String convert(Duration source) {
            return String.valueOf(source.toMinutes());
        }
    }

    public static class TitleConverter implements Converter<TitleSerializable, String> {

        public static final TitleConverter INSTANCE = new TitleConverter();

        protected TitleConverter() {
        }

        @Override
        public String convert(TitleSerializable source) {
            return source.getTitle();
        }
    }
}
