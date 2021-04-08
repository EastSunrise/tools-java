package wsg.tools.internet.base.data.support.lang;

import javax.annotation.Nonnull;

/**
 * Validates whether the values are valid longs.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public class LongValidator extends AbstractNumberValidator<Long> {

    public LongValidator() {
        super(Long.class);
    }

    @Override
    protected Long parseValue(@Nonnull String text) {
        return Long.parseLong(text);
    }
}
