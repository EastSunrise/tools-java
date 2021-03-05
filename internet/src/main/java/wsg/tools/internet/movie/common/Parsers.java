package wsg.tools.internet.movie.common;

import java.util.regex.Pattern;
import wsg.tools.common.util.regex.RegexUtils;

/**
 * Common methods to parse text to values.
 *
 * @author Kingen
 * @since 2021/2/16
 */
public final class Parsers {

    private static final Pattern SUBJECT_URL_REGEX = Pattern
        .compile("https://movie\\.douban\\.com/subject/(?<id>\\d+)/");

    private Parsers() {
    }

    public static long parseDbId(String url) {
        return Long.parseLong(RegexUtils.matchesOrElseThrow(SUBJECT_URL_REGEX, url).group("id"));
    }
}
