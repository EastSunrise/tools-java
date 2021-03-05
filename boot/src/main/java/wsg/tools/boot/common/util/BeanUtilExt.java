package wsg.tools.boot.common.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.common.constant.Constants;

/**
 * Utility extension for bean operations.
 *
 * @author Kingen
 * @since 2020/7/12
 */
public class BeanUtilExt {

    private static final String PROPERTY_NAME_CLASS = "class";
    private static final Map<Pair<Class<?>, Class<?>>, List<PropertyCopier>> COPIERS =
        new ConcurrentHashMap<>(Constants.DEFAULT_MAP_CAPACITY);

    public static <From, To> To convert(From from, Class<To> toClass) {
        if (null == from) {
            return null;
        }
        To to = BeanUtils.instantiateClass(toClass);
        copyPropertiesExceptNull(to, from, true, true);
        return to;
    }

    /**
     * Copy properties from an object to another object.
     * <p>
     * Ignore a property if its value of the destination object is not null; Ignore a property if
     * its value of the original object is null. It means that properties of the destination object
     * are more important.
     */
    public static void copyPropertiesExceptNull(Object dest, Object orig) {
        copyPropertiesExceptNull(dest, orig, false, true);
    }

    /**
     * Copy properties from an object to another object.
     *
     * @param replaced   whether replace the non-null values of destination while copying
     * @param ignoreNull whether ignore null-value properties of the original object
     */
    public static void copyPropertiesExceptNull(Object dest, Object orig, boolean replaced,
        boolean ignoreNull) {
        Objects.requireNonNull(dest, "Can't copy properties to a null object.");
        Objects.requireNonNull(orig, "Can't copy properties from a null object.");
        List<PropertyCopier> copiers = getPropertyCopiers(dest.getClass(), orig.getClass());
        for (PropertyCopier copier : copiers) {
            if (ClassUtils.isAssignable(copier.getRequiredType(), copier.getProvideType())) {
                try {
                    Object origValue = copier.getReadMethod().invoke(orig);
                    if (ignoreNull && null == origValue) {
                        continue;
                    }
                    if (replaced || null == copier.getReadDestMethod().invoke(dest)) {
                        copier.getWriteMethod().invoke(dest, origValue);
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new AppException(e);
                }
            }
        }
    }

    /**
     * Get common properties between the two types. Also, the property has to be writeable for
     * destination object.
     */
    private static List<PropertyCopier> getPropertyCopiers(Class<?> dest, Class<?> orig) {
        Pair<Class<?>, Class<?>> key = Pair.of(dest, orig);
        List<PropertyCopier> copiers = COPIERS.get(key);
        if (null == copiers) {
            copiers = new LinkedList<>();
            for (PropertyDescriptor origDesc : getDescriptorsExceptClass(orig, true, false)) {
                PropertyDescriptor destDesc = BeanUtils
                    .getPropertyDescriptor(dest, origDesc.getName());
                if (null != destDesc) {
                    Method writeMethod = destDesc.getWriteMethod();
                    if (null != writeMethod) {
                        copiers
                            .add(new PropertyCopier(writeMethod, origDesc.getReadMethod(),
                                destDesc.getReadMethod()));
                    }
                }
            }
            COPIERS.put(key, copiers);
        }
        return copiers;
    }

    private static List<PropertyDescriptor> getDescriptorsExceptClass(Class<?> type,
        boolean readable,
        boolean writeable) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(type))
            .filter(descriptor -> !PROPERTY_NAME_CLASS.equals(descriptor.getName()))
            .filter(descriptor -> !readable || null != descriptor.getReadMethod())
            .filter(descriptor -> !writeable || null != descriptor.getWriteMethod())
            .collect(Collectors.toList());
    }

    /**
     * Get all properties and values of the given object.
     */
    public static Map<String, Object> convertToMap(Object o, boolean ignoreNull) {
        Objects.requireNonNull(o, "Can't convert a null object to map.");
        Map<String, Object> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (PropertyDescriptor descriptor : getDescriptorsExceptClass(o.getClass(), true, false)) {
            Object value;
            try {
                value = descriptor.getReadMethod().invoke(o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AppException(e);
            }
            if (!ignoreNull || null != value) {
                map.put(descriptor.getName(), value);
            }
        }
        return map;
    }

    private static class PropertyCopier {

        private final Method writeMethod;
        private final Method readMethod;
        private final Method readDestMethod;
        private final Class<?> requiredType;
        private final Class<?> provideType;

        PropertyCopier(Method writeMethod, Method readMethod, Method readDestMethod) {
            this.writeMethod = writeMethod;
            this.readMethod = readMethod;
            this.readDestMethod = readDestMethod;
            this.requiredType = writeMethod.getParameterTypes()[0];
            this.provideType = readMethod.getReturnType();
        }

        private Method getWriteMethod() {
            return writeMethod;
        }

        private Method getReadMethod() {
            return readMethod;
        }

        private Method getReadDestMethod() {
            return readDestMethod;
        }

        private Class<?> getRequiredType() {
            return requiredType;
        }

        private Class<?> getProvideType() {
            return provideType;
        }
    }
}
