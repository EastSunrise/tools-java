package wsg.tools.internet.video.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.internet.video.jackson.annotation.JoinedValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Deserialize joined string into list.
 *
 * @author Kingen
 * @since 2020/9/2
 */
public class JoinedValueDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

    public static final JoinedValueDeserializer INSTANCE = new JoinedValueDeserializer();
    private String separator = "";
    private JavaType targetType;
    private BeanProperty property;

    protected JoinedValueDeserializer() {
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (targetType.isCollectionLikeType() && parser.hasToken(JsonToken.VALUE_STRING)) {
            String text = parser.getText();
            if (StringUtils.isBlank(text)) {
                return null;
            }
            String[] values = StringUtils.split(text, separator);
            ObjectMapper mapper = (ObjectMapper) parser.getCodec();
            List<Object> list = new ArrayList<>();
            for (String value : values) {
                list.add(mapper.convertValue(value.strip(), targetType.getContentType()));
            }
            return list;
        }
        JsonDeserializer<Object> deserializer = context.findContextualValueDeserializer(targetType, property);
        return deserializer.deserialize(parser, context);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        JoinedValue joinedValue = property.getAnnotation(JoinedValue.class);
        if (joinedValue != null) {
            separator = joinedValue.separator();
        }
        targetType = property.getType();
        this.property = property;
        return this;
    }
}
