package wsg.tools.internet.video.jackson.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Obtains a list from a string joined with comma.
 *
 * @author Kingen
 * @since 2020/7/18
 */
public class CommaSeparatedValueDeserializationProblemHandler extends DeserializationProblemHandler {

    public static final CommaSeparatedValueDeserializationProblemHandler INSTANCE = new CommaSeparatedValueDeserializationProblemHandler();
    private static final String DELIMITER = ", ";

    protected CommaSeparatedValueDeserializationProblemHandler() {
    }

    @Override
    public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType, JsonToken token, JsonParser parser, String failureMsg) throws IOException {
        if (JsonToken.VALUE_STRING.equals(token) && targetType.isCollectionLikeType()) {
            String[] values = parser.getText().split(DELIMITER);
            ObjectMapper mapper = (ObjectMapper) parser.getCodec();
            List<Object> list = new ArrayList<>();
            for (String value : values) {
                list.add(readValue(mapper, targetType.getContentType(), value));
            }
            return list;
        }
        return super.handleUnexpectedToken(ctxt, targetType, token, parser, failureMsg);
    }

    private Object readValue(ObjectMapper mapper, JavaType contentType, String value) throws JsonProcessingException {
        final String json = "\"" + value.trim() + "\"";
        return mapper.readValue(json, contentType);
    }
}
