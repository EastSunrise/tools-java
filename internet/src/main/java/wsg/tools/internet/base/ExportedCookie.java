package wsg.tools.internet.base;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Date;

/**
 * Exported cookies.
 *
 * @author Kingen
 * @since 2021/01/07
 */
@Getter
@Setter
public class ExportedCookie {

    private String domain;
    private String path;
    private boolean session;
    private boolean hostOnly;
    private String sameSite;
    private String name;
    private boolean httpOnly;
    private int id;
    private boolean secure;
    private String storeId;
    private String value;
    @JsonDeserialize(using = NumericDateDeserializer.class)
    private Date expirationDate;

    /**
     * Deserialize a timestamp to {@link Date}.
     *
     * @author Kingen
     * @since 2021/1/7
     */
    public static class NumericDateDeserializer extends StdDeserializer<Date> {

        protected NumericDateDeserializer() {
            super(Date.class);
        }

        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return new Date(p.getLongValue() * 1000);
            } else if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
                return new Date(Double.valueOf(p.getValueAsDouble() * 1000).longValue());
            }
            return ctxt.readValue(p, Date.class);
        }
    }
}