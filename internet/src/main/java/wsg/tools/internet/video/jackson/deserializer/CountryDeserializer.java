package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.base.AbstractCodeDeserializer;
import wsg.tools.internet.video.enums.Country;

/**
 * Deserialize a code to an object of {@link Country}.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class CountryDeserializer extends AbstractCodeDeserializer<String, Country> {
    public CountryDeserializer() {
        super(String.class);
    }

    @Override
    public Country toNonNullT(String s) {
        return Country.ofCode(s);
    }
}
