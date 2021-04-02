package wsg.tools.internet.base.data.support;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.data.EntityValidatorContext;
import wsg.tools.internet.base.data.Validator;
import wsg.tools.internet.base.data.support.lang.DoubleValidator;
import wsg.tools.internet.base.data.support.lang.FloatValidator;
import wsg.tools.internet.base.data.support.lang.IntValidator;
import wsg.tools.internet.base.data.support.lang.LongValidator;
import wsg.tools.internet.base.data.support.lang.StringValidator;
import wsg.tools.internet.base.data.support.time.DurationValidator;
import wsg.tools.internet.base.data.support.util.TypeValidator;
import wsg.tools.internet.base.data.support.util.URLValidator;
import wsg.tools.internet.common.UnexpectedContentException;

/**
 * A basic context which includes common validators and is able to add extended validators.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public class BasicEntityValidatorContext implements EntityValidatorContext {

    private final Map<String, Validator<?>> propertyValidators = new HashMap<>(0);
    private final Map<Class<?>, Validator<?>> typeValidators = new HashMap<>(0);

    public BasicEntityValidatorContext() {
    }

    /**
     * Creates a basic context.
     */
    @Nonnull
    @Contract(" -> new")
    public static BasicEntityValidatorContext create() {
        return new BasicEntityValidatorContext();
    }

    /**
     * Sets a specific validator for the specific property.
     *
     * @param property  name of the property
     * @param validator the validator to be used
     * @return current context
     */
    public BasicEntityValidatorContext setValidator(String property,
        @Nonnull Validator<?> validator) {
        propertyValidators.put(property, validator);
        return this;
    }

    /**
     * Sets a specific validator for the specific type.
     *
     * @param type      type whose validator is to be set
     * @param validator the validator to be used
     * @return current context
     */
    public BasicEntityValidatorContext setValidator(Class<?> type,
        @Nonnull Validator<?> validator) {
        typeValidators.put(type, validator);
        return this;
    }

    @Nonnull
    @Override
    public Validator<?> getValidator(@Nonnull String property, @Nonnull Class<?> clazz) {
        Validator<?> validator = propertyValidators.get(property);
        if (validator != null) {
            return validator;
        }
        validator = typeValidators.get(clazz);
        if (validator != null) {
            return validator;
        }
        validator = Lazy.DEFAULT_VALIDATORS.get(clazz);
        if (validator != null) {
            return validator;
        }
        return new TypeValidator();
    }

    @Override
    public void handleException(Class<?> clazz, String property, Class<?> propertyType) {
        throw new UnexpectedContentException("Unhandled unexpected problem");
    }

    private static final class Lazy {

        private static final Map<Class<?>, Validator<?>> DEFAULT_VALIDATORS = new HashMap<>(16);

        static {
            DEFAULT_VALIDATORS.put(String.class, new StringValidator());
            DEFAULT_VALIDATORS.put(int.class, new IntValidator());
            DEFAULT_VALIDATORS.put(Integer.class, new IntValidator());
            DEFAULT_VALIDATORS.put(long.class, new LongValidator());
            DEFAULT_VALIDATORS.put(Long.class, new LongValidator());
            DEFAULT_VALIDATORS.put(float.class, new FloatValidator());
            DEFAULT_VALIDATORS.put(Float.class, new FloatValidator());
            DEFAULT_VALIDATORS.put(double.class, new DoubleValidator());
            DEFAULT_VALIDATORS.put(Double.class, new DoubleValidator());
            DEFAULT_VALIDATORS.put(Duration.class, new DurationValidator());
            DEFAULT_VALIDATORS.put(URL.class, new URLValidator());
        }
    }
}
