package wsg.tools.common.lang;

import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.util.regex.RegexUtils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Extension of utility for number operations.
 *
 * @author Kingen
 * @since 2020/9/5
 */
public class NumberUtilsExt {

    private static final char NUMBER_SEPARATOR = ',';
    private static final Pattern COMMA_NUMBER_REGEX = Pattern.compile("[1-9]\\d{0,2}(,?\\d{3})*");

    public static long parseCommaSeparatedNumber(String text) {
        Objects.requireNonNull(text, "Text can't be null");
        RegexUtils.matchesOrElseThrow(COMMA_NUMBER_REGEX, text);
        return Long.parseLong(StringUtils.remove(text, NUMBER_SEPARATOR));
    }
}
