package wsg.tools.common.io.excel;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.io.excel.reader.BaseCellToSetter;
import wsg.tools.common.io.excel.writer.BaseCellFromGetter;
import wsg.tools.common.util.function.GetterFunction;
import wsg.tools.common.util.function.SetterBiConsumer;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility to create an excel template quickly.
 *
 * @author Kingen
 * @since 2020/7/23
 */
public class ExcelTemplate<T> {
    private final LinkedHashMap<String, BaseCellFromGetter<T, ?>> writers = new LinkedHashMap<>(Constants.DEFAULT_MAP_CAPACITY);
    private final LinkedHashMap<String, BaseCellToSetter<T, ?>> readers = new LinkedHashMap<>(Constants.DEFAULT_MAP_CAPACITY);

    private ExcelTemplate() {
    }

    public static ExcelTemplate<Map<String, Object>> ofMap(String... headers) {
        ExcelTemplate<Map<String, Object>> template = builder();
        for (String header : headers) {
            template.putGetter(header, map -> map.get(header));
            template.putSetter(header, (map, o) -> map.put(header, o), Object.class);
        }
        return template;
    }

    /**
     * Create a template including specified properties of the given type.
     */
    public static <T> ExcelTemplate<T> create(Class<T> clazz, String... properties) throws IntrospectionException {
        Map<String, PropertyDescriptor> descriptors = Arrays.stream(Introspector.getBeanInfo(clazz).getPropertyDescriptors())
                .collect(Collectors.toMap(FeatureDescriptor::getName, descriptor -> descriptor));
        if (ArrayUtils.isEmpty(properties)) {
            properties = descriptors.keySet().toArray(new String[0]);
        }
        ExcelTemplate<T> builder = ExcelTemplate.builder();
        for (String property : properties) {
            PropertyDescriptor descriptor = descriptors.get(property);
            if (descriptor == null) {
                throw new IllegalArgumentException(String.format("%s doesn't contain a property of '%s'.", clazz, property));
            }
            final Method readMethod = descriptor.getReadMethod();
            final Method writeMethod = descriptor.getWriteMethod();
            final Class<?> propertyType = descriptor.getPropertyType();
            if (readMethod != null) {
                builder.putGetter(property, t -> {
                    try {
                        return readMethod.invoke(t);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
            }
            if (writeMethod != null) {
                builder.putSetter(property, (t, o) -> {
                    try {
                        writeMethod.invoke(t, o);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }, propertyType);
            }
        }
        return builder;
    }

    public static <T> ExcelTemplate<T> builder() {
        return new ExcelTemplate<>();
    }

    public <V> ExcelTemplate<T> putWriter(String header, BaseCellFromGetter<T, V> writer) {
        writers.put(header, writer);
        return this;
    }

    public <V> ExcelTemplate<T> putReader(String header, BaseCellToSetter<T, V> reader) {
        readers.put(header, reader);
        return this;
    }

    public <V> ExcelTemplate<T> put(String header, BaseCellFromGetter<T, V> writer, BaseCellToSetter<T, V> reader) {
        return putWriter(header, writer).putReader(header, reader);
    }

    public <V> ExcelTemplate<T> putGetter(String header, GetterFunction<T, V> getter) {
        return putWriter(header, new BaseCellFromGetter<T, V>() {
            @Override
            public V getValue(T t) {
                return getter.getValue(t);
            }
        });
    }

    public <V> ExcelTemplate<T> putSetter(String header, SetterBiConsumer<T, V> setter, Class<V> clazz) {
        return putReader(header, new BaseCellToSetter<>(clazz) {
            @Override
            public void setValue(T t, V v) {
                setter.setValue(t, v);
            }
        });
    }

    public <V> ExcelTemplate<T> putSetter(String header, SetterBiConsumer<T, V> setter, TypeReference<V> typeReference) {
        return putReader(header, new BaseCellToSetter<>(typeReference) {
            @Override
            public void setValue(T t, V v) {
                setter.setValue(t, v);
            }
        });
    }

    public <V> ExcelTemplate<T> put(String header, GetterFunction<T, V> getter, SetterBiConsumer<T, V> setter, Class<V> clazz) {
        return putGetter(header, getter).putSetter(header, setter, clazz);
    }

    public <V> ExcelTemplate<T> put(String header, GetterFunction<T, V> getter, SetterBiConsumer<T, V> setter, TypeReference<V> typeReference) {
        return putGetter(header, getter).putSetter(header, setter, typeReference);
    }

    public LinkedHashMap<String, BaseCellFromGetter<T, ?>> getWriters() {
        return writers;
    }

    public LinkedHashMap<String, BaseCellToSetter<T, ?>> getReaders() {
        return readers;
    }
}
