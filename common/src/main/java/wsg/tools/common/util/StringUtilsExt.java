package wsg.tools.common.util;

import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.SignEnum;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Extended utility of {@link org.apache.commons.lang3.StringUtils}.
 *
 * @author Kingen
 * @since 2020/7/18
 */
public class StringUtilsExt {

    private static final Pattern CHINESE_REGEX = Pattern.compile("[\u4E00-\u9FBF]");
    private static final Set<Character> SPECIAL_CHARS = Set.of('?', '*', '.', '(', ')');

    /**
     * Encode a string as a pattern.
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
     * Remove all occurrences the given search sign.
     */
    public static String remove(final String text, final SignEnum search) {
        return StringUtils.remove(text, search.getC());
    }

    /**
     * Replace all occurrences of the given search sign with the given replace sign.
     */
    public static String replace(final String text, final SignEnum search, SignEnum replace) {
        return StringUtils.replaceChars(text, search.getC(), replace.getC());
    }

    /**
     * Replace chars which are not permit to name a file.
     * <p>
     * Attention: the colon after the root will be replaced if an absolute path is input.
     *
     * @param filename not an absolute path
     */
    public static String toFilename(final String filename) {
        String result = filename;
        for (SignEnum sign : SignEnum.NOT_PERMIT_SIGNS_FOR_FILENAME) {
            result = StringUtils.replace(result, sign.toString(), SignEnum.HASH + "" + ((int) sign.getC()));
        }
        return result;
    }
}
