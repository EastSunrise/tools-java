package wsg.tools.internet.base.data.support;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.EntityValidatorContext;
import wsg.tools.internet.base.data.Validator;
import wsg.tools.internet.common.UnexpectedContentException;

/**
 * A basic context which includes common validators and is able to add extended validators.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public class BasicEntityValidatorContext implements EntityValidatorContext {

    private final Map<String, Validator> propertyValidators = new HashMap<>(0);
    private final Map<Class<?>, Validator> typeValidators = new HashMap<>(0);

    /**
     * Sets a specific validator for the specific property.
     *
     * @param property  name of the property
     * @param validator the validator to be used
     * @return current context
     */
    public BasicEntityValidatorContext setValidator(String property, @Nonnull Validator validator) {
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
    public BasicEntityValidatorContext setValidator(Class<?> type, @Nonnull Validator validator) {
        typeValidators.put(type, validator);
        return this;
    }

    @Nonnull
    @Override
    public Validator getValidator(@Nonnull String property, @Nonnull Class<?> clazz) {
        Validator validator = propertyValidators.get(property);
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
        return new TypeValidator<>(clazz);
    }

    @Override
    public void handleException(Class<?> clazz, String property, Class<?> propertyType) {
        throw new UnexpectedContentException("Unhandled unexpected problem");
    }

    private static final class Lazy {

        private static final Map<Class<?>, Validator> DEFAULT_VALIDATORS = Map.of(
            String.class, new StringValidator(),
            int.class, new IntValidator(),
            Integer.class, new IntValidator(),
            Duration.class, new DurationValidator(),
            URL.class, new URLValidator()
        );
    }
}
