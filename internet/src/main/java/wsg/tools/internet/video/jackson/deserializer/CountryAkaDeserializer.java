package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.AbstractNonNullDeserializer;
import wsg.tools.internet.video.enums.Country;

/**
 * Deserialize a Chinese title or an English text to {@link Country}.
 *
 * @author Kingen
 * @since 2020/6/20
 */
public class CountryAkaDeserializer extends AbstractNonNullDeserializer<Country, String> {

    public static final CountryAkaDeserializer INSTANCE = new CountryAkaDeserializer();

    protected CountryAkaDeserializer() {
        super(Country.class, String.class);
    }

    @Override
    public Country apply(String aka) {
        try {
            return Country.ofTitle(aka);
        } catch (IllegalArgumentException e) {
            return Country.ofText(aka);
        }
    }
}
