package wsg.tools.internet.base.data.support;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.data.InvalidValueException;

/**
 * Validates whether the values are valid integers.
 *
 * @author Kingen
 * @since 2021/4/1
 */
@Slf4j
public class IntValidator extends AbstractValidator<Integer> {

    @Nonnull
    @Override
    public Integer convert(@Nonnull Object value) throws InvalidValueException {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            throw new InvalidValueException(e.getMessage());
        }
    }

    /**
     * Describes the range of the integer values.
     */
    @Override
    protected void describe(@Nonnull List<Integer> integers) {
        if (!integers.isEmpty()) {
            int max = integers.stream().max(Integer::compare).orElseThrow();
            int min = integers.stream().min(Integer::compare).orElseThrow();
            log.info("Min: {}", min);
            log.info("Max: {}", max);
        }
    }
}
