package wsg.tools.internet.movie.common;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import wsg.tools.common.util.regex.RegexUtils;

/**
 * Released date of a subject.
 * <p>
 * Attributes: festival releases, city specific releases, limited releases, premiere releases,
 * non-theatrical releases, re-releases, language regions in Switzerland, etc.
 *
 * @author Kingen
 * @since 2020/9/4
 */
public class Release {

    private static final Pattern RELEASE_REGEX =
        Pattern.compile("(?<l>(?<y>\\d{4})-(?<m>\\d{2})-(?<d>\\d{2}))(\\((?<c>[^()\\d]+)\\))?");
    private final LocalDate date;
    private final String comment;

    /**
     * Constructs an instance from a text matching the pattern.
     */
    public Release(@Nonnull String text) {
        Matcher matcher = RegexUtils.matchesOrElseThrow(RELEASE_REGEX, text);
        this.date = LocalDate.parse(matcher.group("l"));
        this.comment = matcher.group("c");
    }

    public static Release of(String text) {
        if (text == null) {
            return null;
        }
        return new Release(text);
    }

    public LocalDate getDate() {
        return date;
    }

    @Nullable
    public String getComment() {
        return comment;
    }
}
