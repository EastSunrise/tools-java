package wsg.tools.boot.common;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import wsg.tools.boot.pojo.base.AppException;
import wsg.tools.common.constant.Constants;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Utility extension for bean operations.
 *
 * @author Kingen
 * @since 2020/7/12
 */
public class BeanUtilExt {

    private static final String PROPERTY_NAME_CLASS = "class";
    private static final Map<Pair<Class<?>, Class<?>>, List<PropertyCopier>> COPIERS = new ConcurrentHashMap<>();

    public static <From, To> To convert(From from, Class<To> toClass) {
        if (from == null) {
            return null;
        }
        To to = BeanUtils.instantiateClass(toClass);
        copyPropertiesExceptNull(to, from, true, true);
        return to;
    }

    /**
     * Copy properties from an object to another object.
     * <p>
     * Ignore a property if its value of the destination object is not null;
     * Ignore a property if its value of the original object is null.
     * It means that properties of the destination object are more important.
     */
    public static void copyPropertiesExceptNull(final Object dest, final Object orig) {
        copyPropertiesExceptNull(dest, orig, false, true);
    }

    /**
     * Copy properties from an object to another object.
     *
     * @param replaced   whether replace the non-null values of destination while copying
     * @param ignoreNull whether ignore null-value properties of the original object
     */
    public static void copyPropertiesExceptNull(final Object dest, final Object orig, boolean replaced, boolean ignoreNull) {
        Objects.requireNonNull(dest);
        Objects.requireNonNull(orig);
        List<PropertyCopier> copiers = getPropertyCopiers(dest.getClass(), orig.getClass());
        for (PropertyCopier copier : copiers) {
            if (ClassUtils.isAssignable(copier.requiredType, copier.provideType)) {
                try {
                    Object origValue = copier.readMethod.invoke(orig);
                    if (ignoreNull && origValue == null) {
                        continue;
                    }
                    if (replaced || copier.readDestMethod.invoke(dest) == null) {
                        copier.writeMethod.invoke(dest, origValue);
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new AppException(e);
                }
            }
        }
    }

    /**
     * Get all properties and values of the given type.
     */
    public static Map<String, Object> convertToMap(Object o, boolean ignoreNull) {
        Objects.requireNonNull(o);
        Map<String, Object> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (PropertyDescriptor descriptor : getDescriptorsExceptClass(o.getClass(), true, false)) {
            Object value;
            try {
                value = descriptor.getReadMethod().invoke(o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AppException(e);
            }
            if (!ignoreNull || value != null) {
                map.put(descriptor.getName(), value);
            }
        }
        return map;
    }

    /**
     * Get common properties between the two types.
     * Also, the property has to be writeable for destination object.
     */
    private static List<PropertyCopier> getPropertyCopiers(Class<?> dest, Class<?> orig) {
        Pair<Class<?>, Class<?>> key = Pair.of(dest, orig);
        List<PropertyCopier> copiers = COPIERS.get(key);
        if (copiers == null) {
            copiers = new LinkedList<>();
            for (PropertyDescriptor origDesc : getDescriptorsExceptClass(orig, true, false)) {
                PropertyDescriptor destDesc = BeanUtils.getPropertyDescriptor(dest, origDesc.getName());
                if (destDesc != null) {
                    Method writeMethod = destDesc.getWriteMethod();
                    if (writeMethod != null) {
                        copiers.add(new PropertyCopier(writeMethod, origDesc.getReadMethod(), destDesc.getReadMethod()));
                    }
                }
            }
            COPIERS.put(key, copiers);
        }
        return copiers;
    }

    private static List<PropertyDescriptor> getDescriptorsExceptClass(Class<?> type, boolean readable, boolean writeable) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(type))
                .filter(d -> !PROPERTY_NAME_CLASS.equals(d.getName())
                        && (!readable || d.getReadMethod() != null)
                        && (!writeable || d.getWriteMethod() != null))
                .collect(Collectors.toList());
    }

    static class PropertyCopier {
        final Method writeMethod;
        final Method readMethod;
        final Method readDestMethod;
        final Class<?> requiredType;
        final Class<?> provideType;

        public PropertyCopier(Method writeMethod, Method readMethod, Method readDestMethod) {
            this.writeMethod = writeMethod;
            this.readMethod = readMethod;
            this.readDestMethod = readDestMethod;
            this.requiredType = writeMethod.getParameterTypes()[0];
            this.provideType = readMethod.getReturnType();
        }
    }
}
