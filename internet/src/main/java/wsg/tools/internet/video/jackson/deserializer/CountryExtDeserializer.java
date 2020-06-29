package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.base.AbstractStringDeserializer;
import wsg.tools.internet.video.enums.Country;

/**
 * Deserialize a Chinese title or an English text to {@link Country}.
 *
 * @author Kingen
 * @since 2020/6/20
 */
public class CountryExtDeserializer extends AbstractStringDeserializer<Country> {

    @Override
    public Country toNonNullT(String title) {
        try {
            return Country.ofTitle(title);
        } catch (IllegalArgumentException e) {
            return Country.ofText(title);
        }
    }
}
