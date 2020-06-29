package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.base.AbstractCodeDeserializer;
import wsg.tools.internet.video.enums.Language;

/**
 * Deserialize a code to an object of {@link Language}.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class LanguageDeserializer extends AbstractCodeDeserializer<String, Language> {
    public LanguageDeserializer() {
        super(String.class);
    }

    @Override
    public Language toNonNullT(String s) {
        return Language.ofCode(s);
    }
}
