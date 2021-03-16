package wsg.tools.boot.pojo.entity;

import javax.annotation.Nonnull;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.internet.info.adult.common.SerialNumHeader;
import wsg.tools.internet.info.adult.common.SerialNumber;

/**
 * Utility to handle entities.
 *
 * @author Kingen
 * @since 2021/3/10
 */
public final class EntityUtils {

    private static final long HEADER_BASE = 1_000_000_000;

    private EntityUtils() {
    }

    /**
     * Transfers a serial number of an adult entry to a unique long identifier.
     * <p>
     * A serial number is separated to header part and number part.
     *
     * @return the transferred result. From right to left, the first digit of the result represents
     * the length of the number part, then the next next digits of the result represents the number
     * part, and finally, the remaining digits of the result is filled with the {@link
     * SerialNumHeader#getCode()} which is deserialized from the header part.
     */
    public static long serialize(@Nonnull String serialNum) {
        SerialNumber serialNumber = SerialNumber.of(serialNum);
        SerialNumHeader header = serialNumber.getHeader();
        String number = serialNumber.getNumber();
        return (header.getCode() * HEADER_BASE + Integer.parseInt(number)) * 10 + number.length();
    }

    /**
     * @see #serialize(String)
     */
    public static String deserialize(long id) {
        long length = id % 10;
        id /= 10;
        long number = id % HEADER_BASE;
        long header = id / HEADER_BASE;
        SerialNumHeader anEnum = EnumUtilExt.deserializeCode((int) header, SerialNumHeader.class);
        String format = "%s-%0" + length + "d";
        return String.format(format, anEnum.getHeaders()[0], number);
    }
}
