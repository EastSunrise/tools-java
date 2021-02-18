package wsg.tools.internet.video.site.douban;

import wsg.tools.common.util.regex.RegexUtils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common methods to parse text to values.
 *
 * @author Kingen
 * @since 2021/2/16
 */
public final class Parsers {

    private static final Pattern SUBJECT_URL_REGEX = Pattern.compile("https://movie\\.douban\\.com/subject/(?<id>\\d+)/");
    private static final Pattern DURATION_REGEX = Pattern.compile("(?<h>\\d+)hr|(?<m>\\d+) ?(分钟|min)?(\\([^()\\d]+\\))?");

    public static long parseDbId(String url) {
        return Long.parseLong(RegexUtils.matchesOrElseThrow(SUBJECT_URL_REGEX, url).group("id"));
    }

    public static Duration parseDuration(String text) {
        if (text == null) {
            return null;
        }
        Matcher matcher = RegexUtils.matchesOrElseThrow(DURATION_REGEX, text);
        String m = matcher.group("m");
        if (m != null) {
            return Duration.ofMinutes(Integer.parseInt(m));
        }
        return Duration.ofHours(Integer.parseInt(matcher.group("h")));
    }
}
