package wsg.tools.internet.video.site.imdb.pojo.info;

import lombok.Getter;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.video.site.imdb.pojo.base.BaseImdbInfo;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Info of a released date.
 * <p>
 * Attributes: festival releases, city specific releases, limited releases,
 * premiere releases, non-theatrical releases, re-releases, language regions in Switzerland, etc.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
public class ReleaseInfo extends BaseImdbInfo {

    private static final Pattern RELEASE_REGEX = Pattern.compile("(?<y>\\d{4})-(?<m>\\d{2})-(?<d>\\d{2})(\\((?<r>[^()]+)\\))?");
    private final LocalDate date;
    private final String region;

    public ReleaseInfo(String text) {
        Matcher matcher = RegexUtils.matchesOrElseThrow(RELEASE_REGEX, text);
        this.region = matcher.group("r");
        this.date = LocalDate.of(
                Integer.parseInt(matcher.group("y")),
                Integer.parseInt(matcher.group("m")),
                Integer.parseInt(matcher.group("d"))
        );
    }
}
