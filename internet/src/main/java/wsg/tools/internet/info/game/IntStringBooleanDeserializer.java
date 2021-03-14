package wsg.tools.internet.info.game;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

/**
 * Deserializes '0'/'1' to {@link Boolean}.
 *
 * @author Kingen
 * @since 2021/3/12
 */
public class IntStringBooleanDeserializer extends StdDeserializer<Boolean> {

    private static final String FALSE = "0";
    private static final String TRUE = "1";

    protected IntStringBooleanDeserializer() {
        super(Boolean.class);
    }

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String text = p.getText().trim();
            if (FALSE.equals(text)) {
                return false;
            }
            if (TRUE.equals(text)) {
                return true;
            }
        }
        return (Boolean) ctxt.handleUnexpectedToken(getValueType(), p);
    }
}
