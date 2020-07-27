package wsg.tools.internet.video.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.YearDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Year;
import java.time.format.DateTimeFormatter;

/**
 * Extend {@link YearDeserializer} to handler blank string.
 *
 * @author Kingen
 * @since 2020/7/27
 */
public class YearDeserializerExt extends YearDeserializer {

    public static final YearDeserializerExt INSTANCE = new YearDeserializerExt(null);

    public YearDeserializerExt(DateTimeFormatter formatter) {
        super(formatter);
    }

    @Override
    public Year deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_STRING) && StringUtils.isBlank(parser.getText().trim())) {
            return null;
        }
        return super.deserialize(parser, context);
    }
}
