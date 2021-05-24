package wsg.tools.common.lang;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * Extension of utility for number operations.
 *
 * @author Kingen
 * @since 2020/9/5
 */
public final class NumberUtilsExt {

    private static final char NUMBER_SEPARATOR = ',';

    private NumberUtilsExt() {
    }

    public static long parseCommaSeparatedNumber(String text) {
        Objects.requireNonNull(text, "Text can't be null");
        return Long.parseLong(StringUtils.remove(text, NUMBER_SEPARATOR));
    }
}
