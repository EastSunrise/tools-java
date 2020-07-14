package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.AbstractNonNullDeserializer;
import wsg.tools.internet.video.enums.Country;

/**
 * Deserialize a code to an object of {@link Country}.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class CountryCodeDeserializer extends AbstractNonNullDeserializer<Country, String> {

    public static final CountryCodeDeserializer INSTANCE = new CountryCodeDeserializer();

    protected CountryCodeDeserializer() {
        super(Country.class, String.class);
    }

    @Override
    public Country apply(String code) {
        return Country.ofCode(code);
    }
}
