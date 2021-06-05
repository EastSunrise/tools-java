package wsg.tools.common.jackson;

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
public class JoinedValueDeserializer extends JsonDeserializer<Object> implements
    ContextualDeserializer {

    static final String DEFAULT_SEPARATOR = ",";

    private String separator = DEFAULT_SEPARATOR;
    private JavaType targetType;

    protected JoinedValueDeserializer() {
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
        if (targetType.isCollectionLikeType() && p.hasToken(JsonToken.VALUE_STRING)) {
            String text = p.getText();
            if (StringUtils.isBlank(text)
                && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                return null;
            }
            String[] values = StringUtils.splitByWholeSeparator(text, separator);
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            List<Object> list = new ArrayList<>();
            for (String value : values) {
                list.add(mapper.convertValue(value.strip(), targetType.getContentType()));
            }
            return list;
        }
        return ctxt.handleUnexpectedToken(targetType, p);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty prop) {
        JsonJoinedValue joinedValue = prop.getAnnotation(JsonJoinedValue.class);
        JoinedValueDeserializer deserializer = new JoinedValueDeserializer();
        if (joinedValue != null) {
            deserializer.separator = joinedValue.separator();
        }
        deserializer.targetType = prop.getType();
        return deserializer;
    }
}
