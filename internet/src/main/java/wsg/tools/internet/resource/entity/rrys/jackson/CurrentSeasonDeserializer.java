package wsg.tools.internet.resource.entity.rrys.jackson;

import com.fasterxml.jackson.databind.DeserializationContext;
import wsg.tools.common.jackson.deserializer.AbstractStringDeserializer;
import wsg.tools.common.util.regex.RegexUtils;

import java.util.regex.Pattern;

/**
 * Deserialize current season.
 *
 * @author Kingen
 * @since 2020/12/24
 */
public class CurrentSeasonDeserializer extends AbstractStringDeserializer<Integer> {

    private static final Pattern SEASON_REGEX = Pattern.compile("第(?<s>\\d+)季");

    protected CurrentSeasonDeserializer() {
        super(Integer.class);
    }

    @Override
    protected Integer parseText(String text, DeserializationContext context) {
        return Integer.parseInt(RegexUtils.matchesOrElseThrow(SEASON_REGEX, text).group("s"));
    }
}
