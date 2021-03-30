package wsg.tools.common.io.excel;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.ParameterizedTypeReference;

/**
 * Utility to create an excel template quickly.
 *
 * @author Kingen
 * @since 2020/7/23
 */
public final class ExcelTemplate<T> {

    private final LinkedHashMap<String, CellFromGetter<T, ?>> writers =
        new LinkedHashMap<>(Constants.DEFAULT_MAP_CAPACITY);
    private final LinkedHashMap<String, CellToSetter<T, ?>> readers =
        new LinkedHashMap<>(Constants.DEFAULT_MAP_CAPACITY);

    private ExcelTemplate() {
    }

    public static <V> ExcelTemplate<Map<String, V>> ofMap(Map<String, Class<? extends V>> classes) {
        ExcelTemplate<Map<String, V>> template = builder();
        for (Map.Entry<String, Class<? extends V>> entry : classes.entrySet()) {
            String header = entry.getKey();
            template.putWriter(header, new CellFromGetter<>(map -> map.get(header)));
            template
                .putReader(header,
                    new CellToSetter<>(entry.getValue(), (map, o) -> map.put(header, o)));
        }
        return template;
    }

    /**
     * Create a template including specified properties of the given type.
     *
     * @param properties include all properties if null
     */
    public static <T> ExcelTemplate<T> create(Class<T> clazz, String... properties)
        throws IntrospectionException {
        Map<String, PropertyDescriptor> descriptors =
            Arrays.stream(Introspector.getBeanInfo(clazz).getPropertyDescriptors())
                .collect(Collectors.toMap(FeatureDescriptor::getName, descriptor -> descriptor));
        if (ArrayUtils.isEmpty(properties)) {
            properties = descriptors.keySet().toArray(new String[0]);
        }
        ExcelTemplate<T> builder = builder();
        for (String property : properties) {
            PropertyDescriptor descriptor = descriptors.get(property);
            if (descriptor == null) {
                throw new IllegalArgumentException(
                    String.format("%s doesn't contain a property of '%s'.", clazz, property));
            }
            Method readMethod = descriptor.getReadMethod();
            Method writeMethod = descriptor.getWriteMethod();
            if (readMethod != null) {
                builder.putWriter(property, new CellFromGetter<>(t -> {
                    try {
                        return readMethod.invoke(t);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    }
                }));
            }
            if (writeMethod != null) {
                Type parameterType = writeMethod.getGenericParameterTypes()[0];
                if (parameterType instanceof ParameterizedType) {
                    builder.putReader(property,
                        new CellToSetter<>(
                            new ParameterizedTypeReference((ParameterizedType) parameterType),
                            (t, o) -> {
                                try {
                                    writeMethod.invoke(t, o);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new IllegalStateException(e);
                                }
                            }));
                } else {
                    builder.putReader(property,
                        new CellToSetter<>((Class<?>) parameterType, (t, o) -> {
                            try {
                                writeMethod.invoke(t, o);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new IllegalStateException(e);
                            }
                        }));
                }
            }
        }
        return builder;
    }

    public static <T> ExcelTemplate<T> builder() {
        return new ExcelTemplate<>();
    }

    public <V> ExcelTemplate<T> putWriter(String header, CellFromGetter<T, V> writer) {
        writers.put(header, writer);
        return this;
    }

    public <V> ExcelTemplate<T> putReader(String header, CellToSetter<T, V> reader) {
        readers.put(header, reader);
        return this;
    }

    public <V> ExcelTemplate<T> put(String header, CellFromGetter<T, V> writer,
        CellToSetter<T, V> reader) {
        return putWriter(header, writer).putReader(header, reader);
    }

    public LinkedHashMap<String, CellFromGetter<T, ?>> getWriters() {
        return writers;
    }

    public LinkedHashMap<String, CellToSetter<T, ?>> getReaders() {
        return readers;
    }
}
