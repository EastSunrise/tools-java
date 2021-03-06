package wsg.tools.internet.movie.common;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Runtime of a subject.
 *
 * @author Kingen
 * @since 2021/2/20
 */
public final class Runtime {

    private static final Pattern RUNTIME_REGEX = Pattern.compile("(?<m>\\d+) ?(分钟|min)?(\\((?<c>[^()\\d]+)\\))?");
    private static final Pattern RUNTIME_REGEX2 = Pattern.compile("(?<c>[^()\\d]+): (?<m>\\d+) 分钟");
    private static final Pattern RANGE_RUNTIME_REGEX = Pattern.compile("(?<s>\\d+)[-–](?<e>\\d+)分钟");

    private final Duration duration;
    private String comment;

    /**
     * Constructs an instance from a text matching the pattern.
     */
    public Runtime(@Nonnull String text) {
        Matcher matcher = RUNTIME_REGEX.matcher(text);
        if (matcher.matches()) {
            this.duration = Duration.ofMinutes(Integer.parseInt(matcher.group("m")));
            this.comment = matcher.group("c");
            return;
        }

        Matcher matcher2 = RUNTIME_REGEX2.matcher(text);
        if (matcher2.matches()) {
            this.duration = Duration.ofMinutes(Integer.parseInt(matcher2.group("m")));
            this.comment = matcher2.group("c");
            return;
        }

        matcher2 = RANGE_RUNTIME_REGEX.matcher(text);
        if (matcher2.matches()) {
            int sum = Integer.parseInt(matcher2.group("s")) + Integer.parseInt(matcher2.group("e"));
            this.duration = Duration.ofMinutes(sum / 2);
            return;
        }

        throw new IllegalArgumentException(
            String.format("Not matched string: '%s' for %s", text, Runtime.class));
    }

    public static Runtime of(String text) {
        if (text == null) {
            return null;
        }
        return new Runtime(text);
    }

    public Duration getDuration() {
        return duration;
    }

    @Nullable
    public String getComment() {
        return comment;
    }
}
