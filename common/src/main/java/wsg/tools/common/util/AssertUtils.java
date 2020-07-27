package wsg.tools.common.util;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Utility to check objects or conditions before operation
 *
 * @author Kingen
 * @since 2020/6/22
 */
public abstract class AssertUtils {

    /**
     * Returns a {@link RuntimeException}.
     */
    public static RuntimeException runtimeException(Throwable e) {
        Objects.requireNonNull(e);
        return new RuntimeException(e.getMessage(), e);
    }

    /**
     * Check if there is an element of the collection that match the given predicate.
     *
     * @param message detail message used in the exception
     * @return the first one if exists.
     * @throws IllegalArgumentException if there is none.
     */
    public static <T> T findOne(Stream<T> ts, Predicate<? super T> predicate, String message, Object... args) {
        T t = StreamUtils.findFirst(ts, predicate);
        if (t == null) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return t;
    }

    /**
     * Check if the text is full-matched for the pattern
     *
     * @return an matcher if full-matched.
     * @throws IllegalArgumentException if not full-matched
     */
    public static Matcher matches(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return matcher;
        }
        throw new IllegalArgumentException(String.format("Not matched string: '%s' for pattern: '%s'", text, pattern.pattern()));
    }
}
