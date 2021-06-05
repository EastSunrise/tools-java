package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.Fraction;

/**
 * The deserializer that deserializes a string to a {@link Fraction} as a number or a ratio.
 *
 * @author Kingen
 * @since 2021/6/3
 */
public class FractionDeserializer extends StdDeserializer<Fraction> {

    public static final char RATIO_SEPARATOR = ':';

    public FractionDeserializer() {
        super(Fraction.class);
    }

    @Override
    public Fraction deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return null;
        }
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String text = p.getText();
            if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                && StringUtils.isEmpty(text)) {
                return null;
            }
            if ("0/0".equals(text)) {
                return Fraction.ZERO;
            }
            int pos = text.indexOf(RATIO_SEPARATOR);
            if (pos >= 0) {
                int numerator = Integer.parseInt(text.substring(0, pos));
                int denominator = Integer.parseInt(text.substring(pos + 1));
                return Fraction.getFraction(numerator, denominator);
            }
            return Fraction.getFraction(text);
        }
        if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return Fraction.getFraction(p.getDoubleValue());
        }
        return (Fraction) ctxt.handleUnexpectedToken(Fraction.class, p.currentToken(), p,
            "Unexpected token as a fraction.");
    }
}
