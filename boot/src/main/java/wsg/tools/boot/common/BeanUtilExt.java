package wsg.tools.boot.common;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Utility for operations of bean.
 *
 * @author Kingen
 * @since 2020/6/30
 */
public class BeanUtilExt {

    private static Map<Pair<Class<?>, Class<?>>, List<PropertyCopier>> copiersMap = new HashMap<>();

    /**
     * Copy properties from an object to another object.
     * <p>
     * Ignore a property if its value of the destination object is not null;
     * Ignore a property if its value of the original object is null.
     * It means that properties of the destination object are more important.
     */
    public static void copyPropertiesExceptNull(final Object dest, final Object orig) throws InvocationTargetException, IllegalAccessException {
        copyPropertiesExceptNull(dest, orig, false, true);
    }

    /**
     * Copy properties from an object to another object.
     *
     * @param replaced   whether replace the non-null values of destination while copying
     * @param ignoreNull whether ignore null-value properties of the original object
     */
    public static void copyPropertiesExceptNull(final Object dest, final Object orig, boolean replaced, boolean ignoreNull) throws InvocationTargetException, IllegalAccessException {
        Objects.requireNonNull(dest);
        Objects.requireNonNull(orig);
        Pair<Class<?>, Class<?>> key = Pair.of(dest.getClass(), orig.getClass());
        List<PropertyCopier> copiers = copiersMap.get(key);
        if (copiers == null) {
            copiers = getDescriptors(dest.getClass(), orig.getClass());
            copiersMap.put(key, copiers);
        }
        for (PropertyCopier copier : copiers) {
            if (ClassUtils.isAssignable(copier.requiredType, copier.provideType)) {
                Object origValue = copier.readMethod.invoke(orig);
                if (ignoreNull && origValue == null) {
                    continue;
                }
                if (replaced || copier.readDestMethod.invoke(dest) == null) {
                    copier.writeMethod.invoke(dest, origValue);
                }
            }
        }
    }

    /**
     * Get common properties between the two types.
     * Also, the property has to be writeable for destination object.
     */
    private static List<PropertyCopier> getDescriptors(Class<?> dest, Class<?> orig) {
        List<PropertyCopier> copiers = new LinkedList<>();
        Arrays.stream(BeanUtils.getPropertyDescriptors(orig)).forEach(origDesc -> {
            PropertyDescriptor destDesc = BeanUtils.getPropertyDescriptor(dest, origDesc.getName());
            if (destDesc != null) {
                Method writeMethod = destDesc.getWriteMethod();
                if (writeMethod != null) {
                    copiers.add(new PropertyCopier(writeMethod, origDesc.getReadMethod(), destDesc.getReadMethod()));
                }
            }
        });
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
