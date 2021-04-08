package wsg.tools.internet.base.data.support.lang;

import javax.annotation.Nonnull;

/**
 * Validates whether the values are valid doubles.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public class DoubleValidator extends AbstractNumberValidator<Double> {

    public DoubleValidator() {
        super(Double.class);
    }

    @Override
    protected Double parseValue(@Nonnull String text) {
        return Double.parseDouble(text);
    }
}
