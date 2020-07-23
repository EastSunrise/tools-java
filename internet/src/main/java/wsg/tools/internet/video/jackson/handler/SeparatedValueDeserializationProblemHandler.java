package wsg.tools.internet.video.jackson.handler;

import com.fasterxml.jackson.core.JsonParser;
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
public class SeparatedValueDeserializationProblemHandler extends DeserializationProblemHandler {

    private final String delimiter;

    protected SeparatedValueDeserializationProblemHandler(String delimiter) {
        this.delimiter = delimiter;
    }

    public static SeparatedValueDeserializationProblemHandler getInstance(String delimiter) {
        return new SeparatedValueDeserializationProblemHandler(delimiter);
    }

    @Override
    public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType, JsonToken token, JsonParser parser, String failureMsg) throws IOException {
        if (JsonToken.VALUE_STRING.equals(token) && targetType.isCollectionLikeType()) {
            String[] values = parser.getText().split(delimiter);
            ObjectMapper mapper = (ObjectMapper) parser.getCodec();
            List<Object> list = new ArrayList<>();
            for (String value : values) {
                list.add(mapper.convertValue(value.strip(), targetType.getContentType()));
            }
            return list;
        }
        return super.handleUnexpectedToken(ctxt, targetType, token, parser, failureMsg);
    }
}
