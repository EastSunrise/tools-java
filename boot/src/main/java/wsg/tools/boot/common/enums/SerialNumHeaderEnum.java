package wsg.tools.boot.common.enums;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.regex.RegexUtils;

/**
 * Enum for headers of serial numbers.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public enum SerialNumHeaderEnum implements IntCodeSupplier, TextSupplier {
    /**
     * Various headers of serial numbers
     */
    SIRO(1001, "SIRO"),
    GANA_200(1200, "200GANA"),
    SCUTE_229(1229, "229SCUTE"),
    LUXU_259(1259, "259LUXU"),
    ARA_261(1261, "261ARA"),
    DCV_277(1277, "277DCV"),
    MAAN_300(1300, "300MAAN"),
    MIUM_300(2300, "300MIUM"),
    ;

    private static final long HEADER_BASE = 1_000_000_000;
    private static final Pattern SERIAL_NUM_REGEX;

    static {

        String headers = Arrays.stream(values()).map(SerialNumHeaderEnum::getText)
            .collect(Collectors.joining("|"));
        SERIAL_NUM_REGEX = Pattern
            .compile("(?<h>" + headers + ")-(?<n>\\d+)", Pattern.CASE_INSENSITIVE);
    }

    private final int code;
    private final String text;

    SerialNumHeaderEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * Transfers a serial number of an adult entry to a unique lng identifier.
     * <p>
     * A serial number is separated to header part and number part.
     *
     * @return the transferred result. From right to left, the first digit of the result represents
     * the length of the number part, then the next next digits of the result represents the number
     * part, and finally, the remaining digits of the result is filled with the {@link #code} of a
     * {@link SerialNumHeaderEnum} which is deserialized from the header part.
     */
    public static long serialize(@Nonnull String serialNum) {
        Matcher matcher = RegexUtils.matchesOrElseThrow(SERIAL_NUM_REGEX, serialNum);
        SerialNumHeaderEnum header = EnumUtilExt
            .deserializeText(matcher.group("h"), SerialNumHeaderEnum.class, true);
        String numStr = matcher.group("n");
        int number = Integer.parseInt(numStr);
        return (header.code * HEADER_BASE + number) * 10 + numStr.length();
    }

    public static String deserialize(long id) {
        long length = id % 10;
        id /= 10;
        long number = id % HEADER_BASE;
        long header = id / HEADER_BASE;
        SerialNumHeaderEnum anEnum = EnumUtilExt
            .deserializeCode((int) header, SerialNumHeaderEnum.class);
        String format = "%s-%0" + length + "d";
        return String.format(format, anEnum.text, number);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getText() {
        return text;
    }
}
