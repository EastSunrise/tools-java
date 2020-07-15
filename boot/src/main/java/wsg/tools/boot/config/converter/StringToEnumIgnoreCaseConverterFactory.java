package wsg.tools.boot.config.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.domain.Sort.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Convert a string to an instance of {@link Order}.
 *
 * @author Kingen
 * @since 2020/7/14
 */
@Component
public class StringToEnumIgnoreCaseConverterFactory implements ConverterFactory<String, Enum<?>> {

    @Override
    @NonNull
    public <T extends Enum<?>> Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    private static class StringToEnumConverter<T extends Enum<?>> implements Converter<String, T> {

        private final Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(@NonNull String source) {
            if (StringUtils.isBlank(source)) {
                return null;
            }
            T[] ts = enumType.getEnumConstants();
            for (T t : ts) {
                if (t.name().equalsIgnoreCase(source)) {
                    return t;
                }
            }
            throw new IllegalArgumentException(String.format("Unknown name %s for %s", source, enumType));
        }
    }
}
