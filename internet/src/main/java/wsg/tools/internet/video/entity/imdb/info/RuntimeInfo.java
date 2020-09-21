package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.util.AssertUtils;
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
@Setter
public class RuntimeInfo extends BaseImdbInfo {

    private static final Pattern RUNTIME_REGEX = Pattern.compile("([1-9]\\d{0,2}(,?\\d{3})*)( min|分钟)");

    /**
     * null by default
     */
    private RegionEnum region;
    /**
     * in minutes usually
     */
    private Duration duration;

    public RuntimeInfo(String text) {
        Matcher matcher = AssertUtils.matches(RUNTIME_REGEX, text);
        this.duration = Duration.ofMinutes(Integer.parseInt(matcher.group(1).replace(SignEnum.COMMA.toString(), "")));
    }
}
