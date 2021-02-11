package wsg.tools.internet.video.site.imdb.pojo.info;

import lombok.Getter;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.video.enums.RegionEnum;
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

    private static final Pattern RELEASE_REGEX = Pattern.compile("(?<y>\\d{4})-(?<m>\\d{2})-(?<d>\\d{2})(\\((?<r>[^()]*)\\))?");
    private final LocalDate date;
    private RegionEnum region;

    public ReleaseInfo(String text) {
        Matcher matcher = RegexUtils.matchesOrElseThrow(RELEASE_REGEX, text);
        String region = matcher.group("r");
        if (region != null) {
            this.region = EnumUtilExt.deserializeAka(region, RegionEnum.class);
        }
        this.date = LocalDate.of(
                Integer.parseInt(matcher.group("y")),
                Integer.parseInt(matcher.group("m")),
                Integer.parseInt(matcher.group("d"))
        );
    }
}
