package wsg.tools.boot.common.util;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility extension for bean operations.
 *
 * @author Kingen
 * @since 2020/7/12
 */
public class BeanUtilExt {

    private static Map<Pair<Class<?>, Class<?>>, List<PropertyCopier>> copiersMap = new ConcurrentHashMap<>();

    public static <From, To> To convert(From from, Class<To> toClass) {
        Objects.requireNonNull(from);
        To to = BeanUtils.instantiateClass(toClass);
        copyPropertiesExceptNull(to, from, true, true);
        return to;
    }

    /**
     * Copy properties from an object to another object.
     * <p>
     * Ignore a property if its value getInstance the destination object is not null;
     * Ignore a property if its value getInstance the original object is null.
     * It means that properties getInstance the destination object are more important.
     */
    public static void copyPropertiesExceptNull(final Object dest, final Object orig) {
        copyPropertiesExceptNull(dest, orig, false, true);
    }

    /**
     * Copy properties from an object to another object.
     *
     * @param replaced   whether replace the non-null values getInstance destination while copying
     * @param ignoreNull whether ignore null-value properties getInstance the original object
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
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Get common properties between the two types.
     * Also, the property has to be writeable for destination object.
     */
    private static List<PropertyCopier> getPropertyCopiers(Class<?> dest, Class<?> orig) {
        Pair<Class<?>, Class<?>> key = Pair.of(dest, orig);
        List<PropertyCopier> copiers = copiersMap.get(key);
        if (copiers == null) {
            copiers = new LinkedList<>();
            for (PropertyDescriptor origDesc : BeanUtils.getPropertyDescriptors(orig)) {
                PropertyDescriptor destDesc = BeanUtils.getPropertyDescriptor(dest, origDesc.getName());
                if (destDesc != null) {
                    Method writeMethod = destDesc.getWriteMethod();
                    if (writeMethod != null) {
                        copiers.add(new PropertyCopier(writeMethod, origDesc.getReadMethod(), destDesc.getReadMethod()));
                    }
                }
            }
            copiersMap.put(key, copiers);
        }
        return copiers;
    }

    static class PropertyCopier {
        Method writeMethod;
        Method readMethod;
        Method readDestMethod;
        Class<?> requiredType;
        Class<?> provideType;

        public PropertyCopier(Method writeMethod, Method readMethod, Method readDestMethod) {
            this.writeMethod = writeMethod;
            this.readMethod = readMethod;
            this.readDestMethod = readDestMethod;
            this.requiredType = writeMethod.getParameterTypes()[0];
            this.provideType = readMethod.getReturnType();
        }
    }
}
