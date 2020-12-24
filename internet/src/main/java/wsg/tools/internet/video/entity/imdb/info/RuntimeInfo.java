package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;
import wsg.tools.common.lang.NumberUtilsExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbInfo;
import wsg.tools.internet.video.enums.RegionEnum;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Info of runtime.
 * <p>
 * Attributes: such as (cut version).
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
public class RuntimeInfo extends BaseImdbInfo {

    private static final Pattern RUNTIME_REGEX = Pattern.compile("(?<d>[\\d,]+)(-(?<d2>\\d+))?( ?min| ?分钟)?");
    /**
     * in minutes usually
     */
    private final Duration duration;
    /**
     * null by default
     */
    private RegionEnum region;
    private Duration duration2;

    public RuntimeInfo(String text) {
        Matcher matcher = RegexUtils.matchesOrElseThrow(RUNTIME_REGEX, text);
        this.duration = Duration.ofMinutes(NumberUtilsExt.parseCommaSeparatedNumber(matcher.group("d")));
        String d2 = matcher.group("d2");
        if (d2 != null) {
            this.duration2 = Duration.ofMinutes(Long.parseLong(d2));
        }
    }
}
