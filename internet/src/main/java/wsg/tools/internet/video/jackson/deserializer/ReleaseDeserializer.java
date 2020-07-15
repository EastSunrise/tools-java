package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.AbstractNotBlankDeserializer;
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
public class ReleaseDeserializer extends AbstractNotBlankDeserializer<LocalDate> {

    public static final ReleaseDeserializer INSTANCE = new ReleaseDeserializer();

    private static final Pattern RELEASE_REGEX = Pattern.compile("((\\d{4})(-(\\d{2})(-(\\d{2}))?)?)(\\(([^()]+)\\))?");

    protected ReleaseDeserializer() {
        super(LocalDate.class);
    }

    @Override
    protected LocalDate parseText(String text) {
        Matcher matcher = AssertUtils.matches(RELEASE_REGEX, text);
        int month = matcher.group(3) == null ? 1 : Integer.parseInt(matcher.group(4));
        int day = matcher.group(5) == null ? 1 : Integer.parseInt(matcher.group(6));
        return LocalDate.of(Integer.parseInt(matcher.group(2)), month, day);
    }
}
