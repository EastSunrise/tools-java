package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.AbstractNotBlankDeserializer;
import wsg.tools.common.util.AssertUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserialize a text to an instance of {@link LocalDate} as published date.
 *
 * @author Kingen
 * @since 2020/6/20
 */
public class ReleaseExtDeserializer extends AbstractNotBlankDeserializer<LocalDate> {

    private static final Pattern RELEASE_EXT_REGEX = Pattern.compile("(\\d+\\s[A-z]+\\s\\d{4})\\s\\([^()]+\\)");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy").withLocale(Locale.ENGLISH);

    protected ReleaseExtDeserializer() {
        super(LocalDate.class);
    }

    @Override
    protected LocalDate parseText(String text) {
        Matcher matcher = AssertUtils.matches(RELEASE_EXT_REGEX, text);
        return LocalDate.parse(matcher.group(1), FORMATTER);
    }
}
