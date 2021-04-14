package wsg.tools.internet.base.data.support.lang;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.data.Validator;
import wsg.tools.internet.base.data.support.InvalidValueException;

/**
 * Validates strings.
 *
 * @author Kingen
 * @since 2021/3/31
 */
@Slf4j
public class StringValidator extends Validator<String> {

    @Nonnull
    @Override
    public String convert(@Nonnull Object value) throws InvalidValueException {
        if (!(value instanceof String)) {
            throw new InvalidValueException("Not a string");
        }
        return (String) value;
    }

    /**
     * Counts blank texts.
     */
    @Override
    public void describe(@Nonnull List<String> texts) {
        if (!texts.isEmpty()) {
            long count = texts.stream().filter(String::isBlank).count();
            log.info("Count of blank texts: {}", count);
            long unstripped = texts.stream().filter(s -> s.length() != s.strip().length()).count();
            log.info("Count of not stripped texts: {}", unstripped);
        }
    }
}
