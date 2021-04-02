package wsg.tools.internet.base.data;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Represents a validator for values of simple types.
 *
 * @author Kingen
 * @see EntityValidator
 * @since 2021/3/31
 */
public interface Validator {

    /**
     * Validates given values.
     *
     * @param values values to be validated, may contain null values
     */
    void validate(@Nonnull List<?> values);
}
