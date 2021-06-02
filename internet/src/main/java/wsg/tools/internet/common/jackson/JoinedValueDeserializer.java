package wsg.tools.internet.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Deserialize joined string into list.
 *
 * @author Kingen
 * @since 2020/9/2
 */
public class JoinedValueDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

    static final String DEFAULT_SEPARATOR = ",";

    private String separator = DEFAULT_SEPARATOR;
    private JavaType targetType;
    private BeanProperty property;

    protected JoinedValueDeserializer() {
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (targetType.isCollectionLikeType() && parser.hasToken(JsonToken.VALUE_STRING)) {
            String text = parser.getText();
            if (StringUtils.isBlank(text)
                && context.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                return null;
            }
            String[] values = StringUtils.splitByWholeSeparator(text, separator);
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
        JsonJoinedValue joinedValue = property.getAnnotation(JsonJoinedValue.class);
        if (joinedValue != null) {
            separator = joinedValue.separator();
        }
        targetType = property.getType();
        this.property = property;
        return this;
    }
}
