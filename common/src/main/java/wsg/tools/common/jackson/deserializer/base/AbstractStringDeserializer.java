package wsg.tools.common.jackson.deserializer.base;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Deserialize a text to a required object.
 *
 * @author Kingen
 * @since 2020/6/27
 */
public abstract class AbstractStringDeserializer<T> extends AbstractNonNullDeserializer<String, T> {

    public AbstractStringDeserializer() {
        super(String.class);
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext context) throws IOException {
        if (StringUtils.isBlank(p.getText())) {
            return null;
        }
        return toNonNullT(p.getText());
    }
}
