package wsg.tools.internet.video.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Separate a string into list of keywords.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class String2ListDeserializer extends JsonDeserializer<List<String>> {

    private static final String SEPARATOR = ",\n";

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String[] strings = StringUtils.split(text, SEPARATOR);
        return Arrays.stream(strings).map(String::strip).collect(Collectors.toList());
    }
}
