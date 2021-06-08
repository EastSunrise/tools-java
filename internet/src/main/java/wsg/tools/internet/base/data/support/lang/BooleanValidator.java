package wsg.tools.internet.base.data.support.lang;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.data.support.AbstractParsableValidator;
import wsg.tools.internet.base.data.support.Descriptors;

/**
 * Validates whether the values are valid booleans.
 *
 * @author Kingen
 * @since 2021/4/8
 */
public class BooleanValidator extends AbstractParsableValidator<Boolean> {

    public BooleanValidator() {
        super(Boolean.class);
    }

    @Override
    protected Boolean parseText(@Nonnull String text) {
        return Boolean.parseBoolean(text);
    }

    @Override
    public void describe(List<Boolean> values) {
        Descriptors.enumerate(values, Function.identity());
    }
}
