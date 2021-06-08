package wsg.tools.common.lang;

import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Extended utility of {@link org.apache.commons.lang3.StringUtils}.
 *
 * @author Kingen
 * @since 2020/7/18
 */
public final class StringUtilsExt {

    /**
     * Chars which aren't permit to name files on Windows.
     */
    private static final char[] NOT_PERMIT_CHARS_FOR_FILENAME = {':', '*', '?', '"', '<', '>', '|'};
    private static final Pattern CHINESE_REGEX = Pattern.compile("[一-龿]");
    /**
     * Special chars which need to escape in the patterns.
     */
    private static final Set<Character> SPECIAL_CHARS = Set.of('?', '*', '.', '(', ')');
    private static final char[] UNITS = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};
    private static final int TEN = '十';

    private StringUtilsExt() {
    }

    /**
     * Converts a full-width string to half-width one.
     *
     * @param text the string to be converted
     */
    public static String convertFullWidth(@Nonnull String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char ch : text.toCharArray()) {
            // [!-}]
            if (65281 <= ch && 65374 > ch) {
                sb.append((char) (ch - 65248));
                continue;
            }
            // space
            if (12288 == ch) {
                sb.append((char) 32);
                continue;
            }
            if (8226 == ch) {
                sb.append((char) 183);
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Encode a string as a pattern with special chars escaped.
     */
    public static String encodeAsPattern(String str) {
        if (null == str) {
            return null;
        }
        StringBuilder builder = new StringBuilder(str.length());
        for (char c : str.toCharArray()) {
            if (SPECIAL_CHARS.contains(c)) {
                builder.append("\\");
            }
            builder.append(c);
        }
        return builder.toString();
    }

    public static String chineseNumeric(int num) {
        if (0 > num) {
            throw new IllegalArgumentException("Number must be positive.");
        }
        if (0 == num) {
            return String.valueOf(UNITS[0]);
        }
        int ten = UNITS.length;
        if (num < ten * ten) {
            int tens = num / ten;
            int units = num % ten;
            StringBuilder builder = new StringBuilder(3);
            if (1 < tens) {
                builder.append(UNITS[tens]);
            }
            if (0 < tens) {
                builder.append("十");
            }
            if (0 < units) {
                builder.append(UNITS[units]);
            }
            return builder.toString();
        }
        throw new IllegalArgumentException("Number must be smaller than 100.");
    }

    /**
     * Parses a text of Chinese numeric to an integer.
     *
     * @param text the text to parse, less than one hundred
     */
    public static int parseChinesNumeric(String text) {
        AssertUtils.requireNotBlank(text, "the text of chinese numeric to parse");
        int result = 0;
        int cursor = text.length() - 1;
        char ch = text.charAt(cursor);
        int index = ArrayUtils.indexOf(UNITS, ch);
        if (ArrayUtils.INDEX_NOT_FOUND != index) {
            result += index;
            cursor--;
        }
        if (0 > cursor) {
            return result;
        }
        ch = text.charAt(cursor);
        if (TEN != ch) {
            String message = String.format("Can't parse the text '%s' at index %d", text, cursor);
            throw new IllegalArgumentException(message);
        }
        result += 10;
        cursor--;
        if (0 > cursor) {
            return result;
        }
        if (0 < cursor) {
            throw new IllegalArgumentException("The text to parse is too long: " + text);
        }
        ch = text.charAt(cursor);
        index = ArrayUtils.indexOf(UNITS, ch);
        if (0 >= index) {
            String message = String.format("Can't parse the text '%s' at index %d", text, cursor);
            throw new IllegalArgumentException(message);
        }
        return result + (index - 1) * 10;
    }

    /**
     * Check if the string contain a Chinese character.
     */
    public static boolean hasChinese(CharSequence text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return CHINESE_REGEX.matcher(text).find();
    }

    /**
     * Replace chars which are not permit to name a file.
     * <p>
     * Attention: the colon after the root will be replaced if an absolute path is input.
     *
     * @param filename not an absolute path
     */
    public static String toFilename(String filename) {
        String result = filename;
        for (char sign : NOT_PERMIT_CHARS_FOR_FILENAME) {
            result = StringUtils.replace(result, String.valueOf(sign), "#" + ((int) sign));
        }
        return StringUtils.replace(result, "\n", "");
    }
}
