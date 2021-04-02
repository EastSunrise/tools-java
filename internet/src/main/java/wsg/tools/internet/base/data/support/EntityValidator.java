package wsg.tools.internet.base.data.support;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.data.EntityValidatorContext;
import wsg.tools.internet.base.data.Validator;
import wsg.tools.internet.common.UnexpectedContentException;
import wsg.tools.internet.common.UnexpectedException;

/**
 * The class provides basic methods to validate entities of a complex type and properties contained
 * in the type.
 *
 * @param <T> type of entities to be validated
 * @author Kingen
 * @since 2021/3/31
 */
@Slf4j
public final class EntityValidator<T> {

    private static final String GET_CLASS_METHOD = "getClass";

    private final Class<T> clazz;
    private final Map<String, PropertyValidator> validators;

    private EntityValidator(Class<T> clazz, Map<String, PropertyValidator> validators) {
        this.clazz = clazz;
        this.validators = validators;
    }

    /**
     * Constructs a validator for the given type with default context.
     *
     * @throws IntrospectionException if an exception occurs during introspection.
     */
    @Nonnull
    @Contract("_ -> new")
    public static <T> EntityValidator<T> create(Class<T> clazz) throws IntrospectionException {
        return create(clazz, new BasicEntityValidatorContext());
    }

    /**
     * Constructs a validator for the given type with specific context.
     *
     * @throws IntrospectionException if an exception occurs during introspection.
     */
    @Nonnull
    @Contract("_, _ -> new")
    public static <T> EntityValidator<T> create(Class<T> clazz, EntityValidatorContext context)
        throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        Map<String, PropertyValidator> validators = new LinkedHashMap<>(descriptors.length);
        for (PropertyDescriptor descriptor : descriptors) {
            Method getter = descriptor.getReadMethod();
            if (getter != null) {
                // exclude getClass()
                if (GET_CLASS_METHOD.equals(getter.getName()) && getter.getParameterCount() == 0) {
                    continue;
                }
                String property = descriptor.getName();
                Class<?> propertyType = descriptor.getPropertyType();
                Validator<?> validator = null;
                try {
                    validator = context.getValidator(property, propertyType);
                } catch (UnexpectedContentException e) {
                    context.handleException(clazz, property, propertyType);
                    continue;
                }
                validators.put(property, new PropertyValidator(getter, validator));
            }
        }
        return new EntityValidator<>(clazz, validators);
    }

    /**
     * Validates all available properties.
     *
     * @param entities entities to be validated
     */
    public void validate(@Nonnull List<T> entities) {
        validate(entities, validators.keySet().toArray(new String[0]));
    }

    /**
     * Validates the specific properties.
     *
     * @param entities   entities to be validated
     * @param properties properties to be validated
     * @throws IllegalArgumentException if any given property is not contained in the type
     */
    public void validate(@Nonnull List<T> entities, @Nonnull String[] properties) {
        String beanName = clazz.getSimpleName();
        log.info("Starting to validate {}...", beanName);
        for (String property : properties) {
            PropertyValidator pv = validators.get(property);
            if (pv == null) {
                throw new IllegalArgumentException(
                    String.format("'%s' doesn't have a property named '%s'", beanName, property));
            }
            Method getter = pv.getReadMethod();
            List<Object> values = new ArrayList<>(entities.size());
            getter.setAccessible(true);
            try {
                for (T entity : entities) {
                    values.add(getter.invoke(entity));
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new UnexpectedException(e);
            }
            Validator<?> validator = pv.getValidator();
            String simpleName = validator.getClass().getSimpleName();
            log.info("Starting to validate {} of {} by {}...", property, beanName, simpleName);
            validator.validate(values);
        }
    }

    private static final class PropertyValidator {

        private final Method readMethod;
        private final Validator<?> validator;

        private PropertyValidator(Method readMethod, Validator<?> validator) {
            this.readMethod = readMethod;
            this.validator = validator;
        }

        public Method getReadMethod() {
            return readMethod;
        }

        public Validator<?> getValidator() {
            return validator;
        }
    }
}
