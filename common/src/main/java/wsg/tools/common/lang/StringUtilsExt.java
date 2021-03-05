package wsg.tools.common.lang;

import java.util.Set;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Extended utility of {@link org.apache.commons.lang3.StringUtils}.
 *
 * @author Kingen
 * @since 2020/7/18
 */
@UtilityClass
public class StringUtilsExt {

    /**
     * Chars which aren't permit to name files on Windows.
     */
    private final char[] NOT_PERMIT_CHARS_FOR_FILENAME = {':', '*', '?', '"', '<', '>', '|'};
    private final Pattern CHINESE_REGEX = Pattern.compile("[\u4E00-\u9FBF]");
    /**
     * Special chars which need to escape in the patterns.
     */
    private final Set<Character> SPECIAL_CHARS = Set.of('?', '*', '.', '(', ')');
    private final String[] UNITS = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    /**
     * Encode a string as a pattern with special chars escaped.
     */
    public String encodeAsPattern(String str) {
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

    public String chineseNumeric(int num) {
        if (num < 0) {
            throw new IllegalArgumentException("Number must be positive.");
        }
        if (num == 0) {
            return UNITS[0];
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
     * Check if the string contain a Chinese character.
     */
    public boolean hasChinese(String text) {
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
    public String toFilename(String filename) {
        String result = filename;
        for (char sign : NOT_PERMIT_CHARS_FOR_FILENAME) {
            result = StringUtils.replace(result, String.valueOf(sign), "#" + ((int) sign));
        }
        return result;
    }
}
