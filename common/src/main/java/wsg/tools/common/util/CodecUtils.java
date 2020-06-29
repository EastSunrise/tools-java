package wsg.tools.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to encode and decode
 *
 * @author Kingen
 * @since 2020/6/20
 */
public class CodecUtils {

    private static final Pattern UNICODE_REGEX = Pattern.compile("\\\\u(\\p{XDigit}{4})");

    /**
     * Decode string from Unicode to String
     */
    public static String unicodeDecode(String decoded) {
        Matcher matcher = UNICODE_REGEX.matcher(decoded);
        while (matcher.find()) {
            char ch = (char) Integer.parseInt(matcher.group(1), 16);
            decoded = decoded.replace(matcher.group(), ch + "");
        }
        return decoded;
    }
}
