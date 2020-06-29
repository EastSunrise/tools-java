package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.base.AbstractStringDeserializer;
import wsg.tools.common.util.AssertUtils;

import java.time.Duration;
import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserialize a text to an instance of {@link Duration} as runtime.
 *
 * @author Kingen
 * @since 2020/6/20
 */
public class YearExtDeserializer extends AbstractStringDeserializer<Year> {

    private static final Pattern YEAR_REGEX = Pattern.compile("(\\d{4})(–(\\d{4})?)?");

    @Override
    public Year toNonNullT(String text) {
        Matcher matcher = AssertUtils.matches(YEAR_REGEX, text);
        return Year.of(Integer.parseInt(matcher.group(1)));
    }
}
