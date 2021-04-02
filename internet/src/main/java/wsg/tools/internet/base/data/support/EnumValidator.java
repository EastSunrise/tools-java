package wsg.tools.internet.base.data.support;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.data.InvalidValueException;

/**
 * Validates whether the values are enumerable.
 *
 * @author Kingen
 * @since 2021/4/1
 */
@Slf4j
public class EnumValidator extends AbstractValidator<Object> {

    public EnumValidator() {
        super();
    }

    @Nonnull
    @Override
    public Object convert(@Nonnull Object value) throws InvalidValueException {
        return value;
    }

    /**
     * Enumerates distinct values.
     */
    @Override
    protected void describe(@Nonnull List<Object> values) {
        Set<Object> set = new HashSet<>(values);
        log.info("Printing distinct values: {} ...", set.size());
        for (Object value : set) {
            log.info("{}", value);
        }
    }
}
