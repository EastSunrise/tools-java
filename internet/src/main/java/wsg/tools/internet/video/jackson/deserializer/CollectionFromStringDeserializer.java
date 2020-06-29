package wsg.tools.internet.video.jackson.deserializer;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import wsg.tools.common.jackson.deserializer.base.AbstractStringDeserializer;

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
public class CollectionFromStringDeserializer<T> extends AbstractStringDeserializer<Collection<T>> implements ContextualDeserializer {

    private static final String DELIMITER = ", ";

    private BeanProperty property;
    private JsonParseException exception;

    @Override
    public Collection<T> deserialize(JsonParser p, DeserializationContext context) throws IOException {
        JsonToken jsonToken = p.currentToken();
        JavaType javaType = property.getType();
        if (JsonToken.VALUE_STRING.equals(jsonToken)) {
            JsonLocation currentLocation = p.getCurrentLocation();
            String text = p.getText();
            JsonDeserializer<Object> deserializer = context.findNonContextualValueDeserializer(javaType.getContentType());
            if (deserializer instanceof AbstractStringDeserializer) {
                AbstractStringDeserializer<T> stringDeserializer = (AbstractStringDeserializer<T>) deserializer;
                Class<?> rawClass = javaType.getRawClass();
                Collection<T> collection = null;
                if (List.class.isAssignableFrom(rawClass)) {
                    collection = new LinkedList<>();
                } else if (Set.class.isAssignableFrom(rawClass)) {
                    collection = new HashSet<>();
                }
                if (collection != null) {
                    for (String s : text.split(DELIMITER)) {
                        collection.add(stringDeserializer.toNonNullT(s.strip()));
                    }
                    return collection;
                }
            }
            exception = new JsonParseException(p, "Can't parse collection from string.", currentLocation);
            return toNonNullT(text);
        }
        return context.readValue(p, javaType);
    }

    @Override
    public List<T> toNonNullT(String s) throws JsonParseException {
        throw exception;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        this.property = property;
        return this;
    }
}
