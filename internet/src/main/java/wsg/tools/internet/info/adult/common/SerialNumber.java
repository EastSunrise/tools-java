package wsg.tools.internet.info.adult.common;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import wsg.tools.common.util.regex.RegexUtils;

/**
 * The serial number of an adult entry.
 *
 * @author Kingen
 * @since 2021/3/14
 */
public final class SerialNumber {

    private final String header;
    private final String number;

    private SerialNumber(String header, String number) {
        this.header = header;
        this.number = number;
    }

    /**
     * Formats the given text to a standard serial number.
     *
     * @return a standard serial number
     * @throws NullPointerException     if the text is null
     * @throws IllegalArgumentException if the text is not a valid serial number
     */
    public static String format(String text) {
        Objects.requireNonNull(text, "the text of a serial number");
        text = text.toUpperCase(Locale.ROOT);
        RegexUtils.matchesOrElseThrow(Lazy.SERIAL_NUMBER_REGEX, text);
        return text;
    }

    public String getHeader() {
        return header;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return header + "-" + number;
    }

    private static class Lazy {

        private static final Pattern SERIAL_NUMBER_REGEX = Pattern.compile("[A-Z\\d]+-[A-Z\\d]+");
    }
}
