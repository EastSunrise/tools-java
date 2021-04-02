package wsg.tools.internet.base.data;

import javax.annotation.Nonnull;
import wsg.tools.internet.common.UnexpectedContentException;

/**
 * The context when validating entities.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public interface EntityValidatorContext {

    /**
     * Gets a validator for this context by the specific property and its type.
     *
     * @param property name of the property
     * @param clazz    type of the property
     * @return the validator if found or null if not found
     * @throws UnexpectedContentException if the property is unexpected
     */
    @Nonnull
    Validator<?> getValidator(@Nonnull String property, @Nonnull Class<?> clazz);

    /**
     * Handles when no validator is found in this context.
     *
     * @param clazz        type of entities
     * @param property     the unexpected property
     * @param propertyType type of the unexpected property
     */
    void handleException(Class<?> clazz, String property, Class<?> propertyType);
}
