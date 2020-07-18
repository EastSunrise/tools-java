package wsg.tools.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Extended utility of {@link org.apache.commons.lang3.StringUtils}.
 *
 * @author Kingen
 * @since 2020/7/18
 */
public class StringUtilsExt {

    private static final Pattern HAS_CHINESE_REGEX = Pattern.compile(".*[u4E00-u9FA5]+.*");

    /**
     * Check if the string contain a Chinese character,
     */
    public static boolean hasChinese(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return HAS_CHINESE_REGEX.matcher(text).matches();
    }
}
