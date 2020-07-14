package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.AbstractNonNullDeserializer;
import wsg.tools.common.util.AssertUtils;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserialize a text to an instance of {@link LocalDate} as published date.
 *
 * @author Kingen
 * @since 2020/6/20
 */
public class PubDateDeserializer extends AbstractNonNullDeserializer<LocalDate, String> {

    public static final PubDateDeserializer INSTANCE = new PubDateDeserializer();

    private static final Pattern PUB_DATE_REGEX = Pattern.compile("((\\d{4})(-(\\d{2})(-(\\d{2}))?)?)(\\(([^()]+)\\))?");

    protected PubDateDeserializer() {
        super(LocalDate.class, String.class);
    }

    @Override
    public LocalDate apply(String text) {
        Matcher matcher = AssertUtils.matches(PUB_DATE_REGEX, text);
        int month = matcher.group(3) == null ? 1 : Integer.parseInt(matcher.group(4));
        int day = matcher.group(5) == null ? 1 : Integer.parseInt(matcher.group(6));
        return LocalDate.of(Integer.parseInt(matcher.group(2)), month, day);
    }
}
