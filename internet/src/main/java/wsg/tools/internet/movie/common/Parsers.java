package wsg.tools.internet.movie.common;

import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import wsg.tools.common.util.regex.RegexUtils;

/**
 * Common methods to parse text to values.
 *
 * @author Kingen
 * @since 2021/2/16
 */
@UtilityClass
public class Parsers {

    private final Pattern SUBJECT_URL_REGEX = Pattern
        .compile("https://movie\\.douban\\.com/subject/(?<id>\\d+)/");

    public long parseDbId(String url) {
        return Long.parseLong(RegexUtils.matchesOrElseThrow(SUBJECT_URL_REGEX, url).group("id"));
    }
}
