package wsg.tools.internet.video.site.douban;

import wsg.tools.common.util.regex.RegexUtils;

import java.time.Duration;
import java.util.regex.Pattern;

/**
 * Common methods to parse text to values.
 *
 * @author Kingen
 * @since 2021/2/16
 */
public final class Parsers {

    private static final Pattern DURATION_REGEX = Pattern.compile("(?<m>\\d+) ?(分钟|min)(\\([^()]+\\))?");

    public static Duration parseDuration(String text) {
        if (text == null) {
            return null;
        }
        return Duration.ofMinutes(Integer.parseInt(RegexUtils.matchesOrElseThrow(DURATION_REGEX, text).group("m")));
    }
}
