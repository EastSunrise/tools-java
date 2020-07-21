package wsg.tools.common.converter;

import wsg.tools.common.converter.base.BaseConverter;
import wsg.tools.common.converter.base.Converter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory to convert an object to {@link T}.
 *
 * @author Kingen
 * @since 2020/7/21
 */
public abstract class ConvertFactory<T> {

    private final Map<Class<?>, Converter<?, ? extends T>> converters = new ConcurrentHashMap<>(16);

    public ConvertFactory<T> putConverter(BaseConverter<?, ? extends T> converter) {
        if (getConverter(converter.getSourceType()) != null) {
            throw new IllegalArgumentException("Converters are conflict for " + converter.getSourceType());
        }
        converters.put(converter.getSourceType(), converter);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <S> T convertValue(S value) {
        if (value == null) {
            return null;
        }
        Converter<? super S, ? extends T> converter = (Converter<? super S, ? extends T>) getConverter(value.getClass());
        if (converter != null) {
            return converter.convert(value);
        }
        return convertDefault(value);
    }

    /**
     * Default method to convert a {@link S} to {@link T}.
     *
     * @param value source object
     * @return converted object
     */
    abstract <S> T convertDefault(S value);

    private Converter<?, ? extends T> getConverter(Class<?> clazz) {
        if (clazz == null || Object.class == clazz) {
            return null;
        }
        Converter<?, ? extends T> converter = converters.get(clazz);
        if (converter != null) {
            return converter;
        }
        converter = getConverter(clazz.getSuperclass());
        if (converter != null) {
            return converter;
        }
        for (Class<?> type : clazz.getInterfaces()) {
            converter = getConverter(type);
            if (converter != null) {
                return converter;
            }
        }
        return null;
    }
}