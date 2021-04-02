package wsg.tools.internet.base.data.support.lang;

import javax.annotation.Nonnull;

/**
 * Validates whether the values are valid floats.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public class FloatValidator extends NumberValidator<Float> {

    @Override
    protected Float value(@Nonnull Number number) {
        return number.floatValue();
    }

    @Override
    protected Float parseValue(@Nonnull String text) {
        return Float.parseFloat(text);
    }
}
