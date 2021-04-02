package wsg.tools.internet.base.data;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.data.support.InvalidValueException;

/**
 * This class provides a skeletal implementation to validate values and describe their features.
 *
 * @param <R> target type to which the values are converted to
 * @author Kingen
 * @since 2021/3/31
 */
@Slf4j
public abstract class Validator<R> implements Converter<R>, Descriptor<R> {

    private Descriptor<R>[] descriptors;

    protected Validator() {
        this.descriptors = null;
    }

    /**
     * Validates given values of unknown type.
     *
     * @param values values to be validated, may contain null values
     */
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
        if (descriptors != null) {
            for (Descriptor<R> descriptor : descriptors) {
                descriptor.describe(targets);
            }
        }
    }

    /**
     * Default descriptor. Sets {@link #descriptors} if extra descriptors are required.
     *
     * @param values values to be described
     * @see #descriptors
     */
    @Override
    public abstract void describe(List<R> values);

    public Descriptor<R>[] getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(Descriptor<R>[] descriptors) {
        this.descriptors = descriptors;
    }
}
