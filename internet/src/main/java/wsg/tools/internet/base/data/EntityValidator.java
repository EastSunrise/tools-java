package wsg.tools.internet.base.data;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * An extension of {@code Validator} to validate entities of a complex type and properties contained
 * in the type.
 *
 * @param <T> type of entities to be validated
 * @author Kingen
 * @since 2021/4/2
 */
public interface EntityValidator<T> {

    /**
     * Validates all available properties.
     *
     * @param entities entities to be validated
     */
    void validate(@Nonnull List<T> entities);

    /**
     * Validates the specific properties.
     *
     * @param entities   entities to be validated
     * @param properties properties to be validated
     * @throws IllegalArgumentException if any given property is not contained in the type
     */
    void validate(@Nonnull List<T> entities, @Nonnull String[] properties);
}
