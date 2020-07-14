package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.AbstractNonNullDeserializer;
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
public class YearExtDeserializer extends AbstractNonNullDeserializer<Year, String> {

    public static final YearExtDeserializer INSTANCE = new YearExtDeserializer();
    private static final Pattern YEAR_REGEX = Pattern.compile("(\\d{4})(–(\\d{4})?)?");

    protected YearExtDeserializer() {
        super(Year.class, String.class);
    }

    @Override
    public Year apply(String text) {
        Matcher matcher = AssertUtils.matches(YEAR_REGEX, text);
        return Year.of(Integer.parseInt(matcher.group(1)));
    }
}
