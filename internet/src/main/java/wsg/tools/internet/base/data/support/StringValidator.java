package wsg.tools.internet.base.data.support;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.internet.base.data.InvalidValueException;

/**
 * Validates strings.
 *
 * @author Kingen
 * @since 2021/3/31
 */
@Slf4j
public class StringValidator extends AbstractValidator<String> {

    private final boolean printed;

    public StringValidator() {
        this.printed = false;
    }

    public StringValidator(boolean printed) {
        this.printed = printed;
    }

    @Nonnull
    @Override
    public String convert(@Nonnull Object value) throws InvalidValueException {
        if (!(value instanceof String)) {
            throw new InvalidValueException("Not a string");
        }
        return (String) value;
    }

    /**
     * Counts not-blank texts and prints if required.
     */
    @Override
    protected void describe(@Nonnull List<String> texts) {
        List<String> notBlanks = texts.stream().filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        log.info("Count of not-blank texts: {}", notBlanks.size());
        if (printed) {
            log.info("Printing all not-blank texts...");
            for (String text : texts) {
                log.info(text);
            }
        }
    }
}
