package wsg.tools.common.io.csv.support;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.io.csv.CsvSetter;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.function.Getter;

/**
 * A template is a descriptor for part properties of beans that matches the data in format of csv.
 *
 * @param <T> type of beans that be matched with data in format of csv
 * @author Kingen
 * @since 2020/7/23
 */
public final class CsvTemplate<T> {

    private final Map<String, Getter<T, ?>> getters = new LinkedHashMap<>(4);
    private final Map<String, CsvSetter<T>> setters = new LinkedHashMap<>(4);

    private CsvTemplate() {
    }

    /**
     * Creates a template for the given class, including specified properties of the given class.
     *
     * @param clazz      type of beans to be read and written
     * @param mapper     jackson used to deserialize a string to target value
     * @param properties properties to be included in this template. All available properties will
     *                   be included if the array is empty.
     * @throws IllegalArgumentException if any given property is not contained in the class
     * @see BeanInfo
     */
    @Nonnull
    public static <T> CsvTemplate<T> create(Class<T> clazz, ObjectMapper mapper,
        @Nonnull String... properties)
        throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        Map<String, PropertyDescriptor> descriptors = new HashMap<>(properties.length);
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            MapUtilsExt.putIfAbsent(descriptors, propertyDescriptor.getName(), propertyDescriptor);
        }
        if (ArrayUtils.isEmpty(properties)) {
            properties = descriptors.keySet().toArray(new String[0]);
        }
        CsvTemplate<T> template = new CsvTemplate<>();
        TypeFactory factory = mapper.getTypeFactory();
        for (String property : properties) {
            PropertyDescriptor descriptor = descriptors.get(property);
            if (descriptor == null) {
                throw new IllegalArgumentException(
                    String.format("%s doesn't contain a property of '%s'.", clazz, property));
            }
            Method readMethod = descriptor.getReadMethod();
            Method writeMethod = descriptor.getWriteMethod();
            if (readMethod != null) {
                template.putGetter(property, t -> {
                    try {
                        return readMethod.invoke(t);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    }
                });
            }
            if (writeMethod != null) {
                Type type = writeMethod.getGenericParameterTypes()[0];
                template.putSetter(property, (bean, value) -> {
                    JavaType javaType = factory.constructType(type);
                    try {
                        writeMethod.invoke(bean, mapper.convertValue(value, javaType));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    }
                });
            }
        }
        return template;
    }

    /**
     * Creates a template for a map.
     */
    @Nonnull
    public static <V> CsvTemplate<Map<String, V>> createMap(ObjectMapper mapper,
        @Nonnull Map<String, Class<? extends V>> classes) {
        CsvTemplate<Map<String, V>> template = builder();
        TypeFactory factory = mapper.getTypeFactory();
        for (Map.Entry<String, Class<? extends V>> entry : classes.entrySet()) {
            String header = entry.getKey();
            template.putGetter(header, map -> map.get(header));
            template.putSetter(header, (map, value) -> {
                JavaType javaType = factory.constructType(entry.getValue());
                map.put(header, mapper.convertValue(value, javaType));
            });
        }
        return template;
    }

    @Nonnull
    public static <T> CsvTemplate<T> builder() {
        return new CsvTemplate<>();
    }

    public <V> CsvTemplate<T> putGetter(@Nonnull String header, @Nonnull Getter<T, V> getter) {
        getters.put(header, getter);
        return this;
    }

    public <V> CsvTemplate<T> putSetter(@Nonnull String header, @Nonnull CsvSetter<T> setter) {
        setters.put(header, setter);
        return this;
    }

    Map<String, Getter<T, ?>> getGetters() {
        return getters;
    }

    Map<String, CsvSetter<T>> getSetters() {
        return setters;
    }
}
