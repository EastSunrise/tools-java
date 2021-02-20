package wsg.tools.internet.video.common;

import wsg.tools.common.util.regex.RegexUtils;

import java.util.regex.Pattern;

/**
 * Common methods to parse text to values.
 *
 * @author Kingen
 * @since 2021/2/16
 */
public final class Parsers {

    private static final Pattern SUBJECT_URL_REGEX = Pattern.compile("https://movie\\.douban\\.com/subject/(?<id>\\d+)/");

    public static long parseDbId(String url) {
        return Long.parseLong(RegexUtils.matchesOrElseThrow(SUBJECT_URL_REGEX, url).group("id"));
    }
}
