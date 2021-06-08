package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.apache.commons.lang3.math.Fraction;

/**
 * The deserializer that deserializes a string to a {@link Fraction} as a number or a ratio.
 *
 * @author Kingen
 * @since 2021/6/3
 */
public class FractionDeserializer extends StdDeserializer<Fraction> {

    public static final int RATIO_SEPARATOR = ':';

    public FractionDeserializer() {
        super(Fraction.class);
    }

    @Override
    public Fraction deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String text = p.getText();
            if ("0/0".equals(text)) {
                return Fraction.ZERO;
            }
            int pos = text.indexOf(RATIO_SEPARATOR);
            if (0 <= pos) {
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
