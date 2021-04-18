package wsg.tools.boot.common.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Operations of beans.
 *
 * @author Kingen
 * @since 2021/4/16
 */
public class BeanOpt<T> {

    private final Map<String, Method> getters = new HashMap<>();
    private final Map<String, Method> setters = new HashMap<>();

    public BeanOpt(Class<T> clazz) throws IntrospectionException {
        PropertyDescriptor[] descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            Method setter = descriptor.getWriteMethod();
            if (setter != null) {
                setters.put(descriptor.getName(), setter);
            }
            Method getter = descriptor.getReadMethod();
            if (getter != null) {
                getters.put(descriptor.getName(), getter);
            }
        }
    }

    /**
     * Merges values of all properties of the older entity to the newer one.
     *
     * @see #merge(Object, Object, String[])
     */
    public Optional<String> merge(@Nonnull T older, @Nonnull T newer)
        throws InvocationTargetException, IllegalAccessException {
        Set<String> keys = getters.keySet();
        String[] properties = setters.keySet().stream().filter(keys::contains)
            .toArray(value -> new String[0]);
        return merge(older, newer, properties);
    }

    /**
     * Merges values of the given properties of the older entity to the newer one.
     *
     * @param older the older entity from which the properties are read
     * @param newer the newer entity to which the properties are written
     * @return {@code Option#empty()} if succeeds, otherwise returns the name of the property whose
     * values are conflict.
     * @throws InvocationTargetException if any getter of setter method throws an exception when
     *                                   invoking
     * @throws IllegalAccessException    if any getter of setter method is inaccessible
     * @throws IllegalArgumentException  if any property can't be merged
     */
    public Optional<String> merge(@Nonnull T older, @Nonnull T newer, @Nonnull String[] properties)
        throws InvocationTargetException, IllegalAccessException {
        for (String property : properties) {
            Method getter = getters.get(property);
            if (getter == null) {
                throw new IllegalArgumentException("Can't the getter method of " + property);
            }
            Method setter = setters.get(property);
            if (setter == null) {
                throw new IllegalArgumentException("Can't the setter method of " + property);
            }
            Object olderValue = getter.invoke(older);
            if (olderValue == null) {
                continue;
            }
            Object newerValue = getter.invoke(newer);
            if (newerValue == null) {
                setter.invoke(newer, olderValue);
                continue;
            }
            if (newerValue.equals(olderValue)) {
                continue;
            }
            return Optional.of(property);
        }
        return Optional.empty();
    }
}
