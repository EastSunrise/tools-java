package wsg.tools.common.util.regex;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

/**
 * Utility for regex.
 *
 * @author Kingen
 * @since 2020/10/29
 */
@UtilityClass
public class RegexUtils {

    /**
     * Check if the text is full-matched for the pattern.
     *
     * @return an matcher if full-matched.
     * @throws IllegalArgumentException if not full-matched
     */
    public Matcher matchesOrElseThrow(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return matcher;
        }
        throw new IllegalArgumentException(
            String.format("Not matched string: '%s' for pattern: '%s'", text, pattern.pattern()));
    }

    /**
     * If the pattern is found in the text, performs the given action with the value, otherwise does
     * nothing.
     */
    public void ifFind(Pattern pattern, String text, Consumer<Matcher> consumer) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            consumer.accept(matcher);
        }
    }

    /**
     * Check if the pattern is found in the text.
     *
     * @return an matcher if found
     * @throws IllegalArgumentException if not found
     */
    public Matcher findOrElseThrow(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher;
        }
        throw new IllegalArgumentException(
            String.format("Not found pattern: '%s' in the text: '%s'", pattern.pattern(), text));
    }

    /**
     * Check if the text starts with the pattern.
     *
     * @return an matcher if starting with
     * @throws IllegalArgumentException if not start
     */
    public Matcher lookingAtOrElseThrow(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.lookingAt()) {
            return matcher;
        }
        throw new IllegalArgumentException(
            String
                .format("Not start with pattern: '%s' do the text: '%s'", pattern.pattern(), text));
    }
}
