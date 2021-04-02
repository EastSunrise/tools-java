package wsg.tools.internet.base.data.support.lang;

import javax.annotation.Nonnull;

/**
 * Validates whether the values are valid integers.
 *
 * @author Kingen
 * @since 2021/4/1
 */
public class IntValidator extends NumberValidator<Integer> {

    @Override
    protected Integer value(@Nonnull Number number) {
        return number.intValue();
    }

    @Override
    protected Integer parseValue(@Nonnull String text) {
        return Integer.parseInt(text);
    }
}
