package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.util.AssertUtils;

import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Info of year.
 *
 * @author Kingen
 * @since 2020/9/5
 */
@Getter
@Setter
public class YearInfo {

    private static final Pattern YEAR_REGEX = Pattern.compile("(\\d{4})(–(\\d{4})?)?");

    private Year start;
    private Year end;

    public YearInfo(String text) {
        Matcher matcher = AssertUtils.matches(YEAR_REGEX, text);
        this.start = Year.parse(matcher.group(1));
        String end = matcher.group(3);
        if (end != null) {
            this.end = Year.parse(end);
        }
    }
}
