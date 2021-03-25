package wsg.tools.common.lang;

import java.util.Set;
import java.util.regex.Pattern;
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
    private static final char TEN = '十';

    private StringUtilsExt() {
    }

    /**
     * Encode a string as a pattern with special chars escaped.
     */
    public static String encodeAsPattern(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (SPECIAL_CHARS.contains(c)) {
                builder.append("\\");
            }
            builder.append(c);
        }
        return builder.toString();
    }

    public static String chineseNumeric(int num) {
        if (num < 0) {
            throw new IllegalArgumentException("Number must be positive.");
        }
        if (num == 0) {
            return String.valueOf(UNITS[0]);
        }
        int ten = UNITS.length;
        if (num < ten * ten) {
            int tens = num / ten;
            int units = num % ten;
            StringBuilder builder = new StringBuilder();
            if (tens > 1) {
                builder.append(UNITS[tens]);
            }
            if (tens > 0) {
                builder.append("十");
            }
            if (units > 0) {
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
        if (index != ArrayUtils.INDEX_NOT_FOUND) {
            result += index;
            cursor--;
        }
        if (cursor < 0) {
            return result;
        }
        ch = text.charAt(cursor);
        if (ch != TEN) {
            String message = String.format("Can't parse the text '%s' at index %d", text, cursor);
            throw new IllegalArgumentException(message);
        }
        result += 10;
        cursor--;
        if (cursor < 0) {
            return result;
        }
        if (cursor > 0) {
            throw new IllegalArgumentException("The text to parse is too long: " + text);
        }
        ch = text.charAt(cursor);
        index = ArrayUtils.indexOf(UNITS, ch);
        if (index <= 0) {
            String message = String.format("Can't parse the text '%s' at index %d", text, cursor);
            throw new IllegalArgumentException(message);
        }
        return result + (index - 1) * 10;
    }

    /**
     * Check if the string contain a Chinese character.
     */
    public static boolean hasChinese(String text) {
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
        return result;
    }
}
