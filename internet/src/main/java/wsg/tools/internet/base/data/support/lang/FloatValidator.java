package wsg.tools.internet.base.data.support.lang;

import javax.annotation.Nonnull;

/**
 * Validates whether the values are valid floats.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public class FloatValidator extends AbstractNumberValidator<Float> {

    public FloatValidator() {
        super(Float.class);
    }

    @Override
    protected Float parseValue(@Nonnull String text) {
        return Float.parseFloat(text);
    }
}
