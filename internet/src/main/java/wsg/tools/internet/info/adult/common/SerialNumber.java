package wsg.tools.internet.info.adult.common;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;

/**
 * The serial number of an adult entry.
 *
 * @author Kingen
 * @since 2021/3/14
 */
public final class SerialNumber {

    private static final Pattern SERIAL_NUMBER_REGEX;

    static {
        String headers = Arrays.stream(SerialNumHeader.values())
            .map(SerialNumHeader::getHeaders)
            .map(arr -> String.join("|", arr))
            .collect(Collectors.joining("|"));
        SERIAL_NUMBER_REGEX = Pattern
            .compile("(?<h>" + headers + ")-(?<n>\\d+)", Pattern.CASE_INSENSITIVE);
    }

    private final SerialNumHeader header;
    private final String number;

    private SerialNumber(SerialNumHeader header, String number) {
        this.header = header;
        this.number = number;
    }

    public static SerialNumber of(String text) {
        AssertUtils.requireNotBlank(text, "text for serial number");
        Matcher matcher = RegexUtils.matchesOrElseThrow(SERIAL_NUMBER_REGEX, text);
        SerialNumHeader header = EnumUtilExt
            .deserializeAka(matcher.group("h"), SerialNumHeader.class);
        return new SerialNumber(header, matcher.group("n"));
    }

    public SerialNumHeader getHeader() {
        return header;
    }

    public String getNumber() {
        return number;
    }
}
