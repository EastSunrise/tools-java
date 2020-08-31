package wsg.tools.internet.video.jackson.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrap a singleton object which should be a list in most cases.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class SingletonListDeserializationProblemHandler extends DeserializationProblemHandler {

    public static final SingletonListDeserializationProblemHandler INSTANCE = new SingletonListDeserializationProblemHandler();

    protected SingletonListDeserializationProblemHandler() { }

    @Override
    public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType, JsonToken token, JsonParser parser, String failureMsg) throws IOException {
        if (targetType.isCollectionLikeType()) {
            if (JsonToken.START_OBJECT.equals(token) || JsonToken.VALUE_STRING.equals(token)) {
                Object single = parser.readValueAs(targetType.getContentType().getRawClass());
                List<Object> list = new ArrayList<>();
                list.add(single);
                return list;
            }
        }
        return super.handleUnexpectedToken(ctxt, targetType, token, parser, failureMsg);
    }
}
