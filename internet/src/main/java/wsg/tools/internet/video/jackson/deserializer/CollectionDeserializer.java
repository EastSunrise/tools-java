package wsg.tools.internet.video.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import wsg.tools.common.jackson.deserializer.AbstractNonNullDeserializer;

import java.io.IOException;
import java.util.*;

/**
 * Pre-handle the json node before deserialization.
 * <p>
 * If it's a string node, split with {@link #DELIMITER} and deserialize to target object separately.
 * Otherwise, deserialize by default.
 *
 * @author Kingen
 * @since 2020/6/19
 */
public class CollectionDeserializer<T> extends JsonDeserializer<Collection<T>> implements ContextualDeserializer {

    private static final String DELIMITER = ", ";

    private BeanProperty property;

    @Override
    public Collection<T> deserialize(JsonParser p, DeserializationContext context) throws IOException {
        JsonToken jsonToken = p.currentToken();
        JavaType javaType = property.getType();
        if (JsonToken.VALUE_STRING.equals(jsonToken)) {
            String text = p.getText();
            JsonDeserializer<Object> deserializer = context.findNonContextualValueDeserializer(javaType.getContentType());
            if (deserializer instanceof AbstractNonNullDeserializer) {
                AbstractNonNullDeserializer<T, String> stringDeserializer = (AbstractNonNullDeserializer<T, String>) deserializer;
                Class<?> rawClass = javaType.getRawClass();
                Collection<T> collection = null;
                if (List.class.isAssignableFrom(rawClass)) {
                    collection = new LinkedList<>();
                } else if (Set.class.isAssignableFrom(rawClass)) {
                    collection = new HashSet<>();
                }
                if (collection != null) {
                    for (String s : text.split(DELIMITER)) {
                        collection.add(stringDeserializer.apply(s.strip()));
                    }
                    return collection;
                }
            }
            throw new RuntimeException("Can't parse collection from string.");
        }
        return context.readValue(p, javaType);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        this.property = property;
        return this;
    }
}
