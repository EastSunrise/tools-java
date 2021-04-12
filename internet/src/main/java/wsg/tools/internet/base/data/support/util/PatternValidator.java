package wsg.tools.internet.base.data.support.util;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.data.Validator;
import wsg.tools.internet.base.data.support.InvalidValueException;

/**
 * Validates whether the string values matches a pattern.
 *
 * @author Kingen
 * @since 2021/3/6
 */
@Slf4j
public class PatternValidator extends Validator<MatchResult> {

    private final Pattern pattern;
    private final MatcherType type;

    public PatternValidator(@Nonnull Pattern pattern, MatcherType type) {
        this.pattern = pattern;
        this.type = type;
    }

    public PatternValidator(Pattern pattern) {
        this(pattern, MatcherType.MATCHES);
    }

    @Nonnull
    @Override
    public MatchResult convert(@Nonnull Object value) throws InvalidValueException {
        if (value instanceof MatchResult) {
            return (MatchResult) value;
        }
        String text = value.toString();
        Matcher matcher = pattern.matcher(text);
        switch (type) {
            case MATCHES:
                if (matcher.matches()) {
                    return matcher.toMatchResult();
                }
                break;
            case FIND:
                if (matcher.find()) {
                    return matcher.toMatchResult();
                }
                break;
            case LOOKING_AT:
                if (matcher.lookingAt()) {
                    return matcher.toMatchResult();
                }
                break;
            default:
                throw new InvalidValueException("Unknown type of matcher");
        }
        throw new InvalidValueException("Mismatch text");
    }

    @Override
    public void describe(@Nonnull List<MatchResult> results) {
    }

    public enum MatcherType {
        /**
         * @see Matcher#matches()
         */
        MATCHES,
        /**
         * @see Matcher#find()
         */
        FIND,
        /**
         * @see Matcher#lookingAt()
         */
        LOOKING_AT
    }
}
