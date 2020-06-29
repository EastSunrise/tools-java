package wsg.tools.common.jackson.deserializer.base;

import wsg.tools.common.util.AssertUtils;

import java.util.regex.Pattern;

/**
 * Deserialize a string to a number.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public abstract class BaseNumberDeserializer<T> extends AbstractStringDeserializer<T> {

    public static final BaseNumberDeserializer<Long> LONG_DESERIALIZER = new BaseNumberDeserializer<>() {
        @Override
        public Long parseNumber(String s) {
            return Long.parseLong(s);
        }
    };

    private static final Pattern NUMBER_REGEX = Pattern.compile("\\d+(,\\d{3})*");

    @Override
    public T toNonNullT(String s) {
        AssertUtils.matches(NUMBER_REGEX, s);
        return parseNumber(s.replace(",", ""));
    }

    /**
     * Obtains a number from a string
     *
     * @param s the string to parse
     * @return a number
     */
    public abstract T parseNumber(String s);
}
