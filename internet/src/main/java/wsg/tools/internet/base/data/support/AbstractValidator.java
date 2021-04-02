package wsg.tools.internet.base.data.support;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.data.Converter;
import wsg.tools.internet.base.data.InvalidValueException;
import wsg.tools.internet.base.data.Validator;

/**
 * This class provides a skeletal implementation to validate values and print the result by logger.
 *
 * @param <R> target type to which the values are converted to
 * @author Kingen
 * @since 2021/3/31
 */
@Slf4j
public abstract class AbstractValidator<R> implements Validator, Converter<R> {

    @Override
    public void validate(@Nonnull List<?> values) {
        int total = values.size();
        log.info("Total Amount: {}", total);
        List<Object> nonNulls = new ArrayList<>(values.size());
        for (Object value : values) {
            if (value != null) {
                nonNulls.add(value);
            }
        }
        log.warn("Count of nulls: {}", total - nonNulls.size());
        List<R> targets = new ArrayList<>(nonNulls.size());
        for (Object nonNull : nonNulls) {
            try {
                targets.add(convert(nonNull));
            } catch (InvalidValueException e) {
                log.error("{}: {}", e.getMessage(), nonNull);
            }
        }
        log.warn("Count of invalid values: {}", nonNulls.size() - targets.size());
        log.info("Count of valid values: {}", targets.size());
        describe(targets);
    }

    /**
     * Converts the value to target type.
     *
     * @param value the value to be converted, must not be null
     * @return converted value of target type
     * @throws InvalidValueException if the given value is invalid
     */
    @Override
    @Nonnull
    public abstract R convert(@Nonnull Object value) throws InvalidValueException;

    /**
     * Describes features of converted values.
     *
     * @param values valid values of target type
     */
    protected abstract void describe(@Nonnull List<R> values);
}
