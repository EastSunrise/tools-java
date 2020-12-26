package wsg.tools.internet.resource.entity.rrys.jackson;

import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.jackson.deserializer.AbstractStringDeserializer;
import wsg.tools.common.util.regex.RegexUtils;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserialize a string of file size to Double.
 *
 * @author Kingen
 * @since 2020/12/24
 */
public class FileSizeDeserializer extends AbstractStringDeserializer<Double> {

    private static final Pattern REGEX = Pattern.compile("(?<s>[.,\\d]+) ?(?<u>KB|M|MB|G|GB)?");
    private static final Set<String> ZEROS = Set.of("0", "0B");
    private static final Map<String, Integer> UNITS = Map.of("M", 1024, "G", 1024 * 1024);

    protected FileSizeDeserializer() {
        super(Double.class);
    }

    @Override
    protected Double parseText(String text, DeserializationContext context) {
        if (ZEROS.contains(text)) {
            return 0D;
        }
        Matcher matcher = RegexUtils.matchesOrElseThrow(REGEX, text);
        double size = Double.parseDouble(matcher.group("s").replace(',', '.'));
        String unit = matcher.group("u");
        if (unit == null) {
            size *= -1;
        }
        for (Map.Entry<String, Integer> entry : UNITS.entrySet()) {
            if (StringUtils.startsWithIgnoreCase(unit, entry.getKey())) {
                size *= entry.getValue();
            }
        }
        return size;
    }
}
