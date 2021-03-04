package wsg.tools.internet.movie.common;

import wsg.tools.common.util.regex.RegexUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ranged years of series.
 *
 * @author Kingen
 * @since 2020/9/5
 */
public class RangeYear {

    private static final Pattern RANGE_YEAR_REGEX = Pattern.compile("(?<s>\\d{4})(–(?<e>\\d{4})?)?");

    private final int start;
    private final Integer end;

    /**
     * Constructs an instance from a text matching the pattern.
     */
    public RangeYear(@Nonnull String text) {
        Matcher matcher = RegexUtils.matchesOrElseThrow(RANGE_YEAR_REGEX, text);
        this.start = Integer.parseInt(matcher.group("s"));
        String e = matcher.group("e");
        this.end = e == null ? null : Integer.parseInt(e);
    }

    public RangeYear(int start) {
        this.start = start;
        this.end = null;
    }

    public RangeYear(int start, Integer end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    @Nullable
    public Integer getEnd() {
        return end;
    }
}
