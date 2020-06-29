package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.base.AbstractStringDeserializer;
import wsg.tools.internet.video.enums.Language;

/**
 * Deserialize a Chinese title or an English text to {@link Language}.
 *
 * @author Kingen
 * @since 2020/6/20
 */
public class LanguageExtDeserializer extends AbstractStringDeserializer<Language> {

    @Override
    public Language toNonNullT(String text) {
        try {
            return Language.ofTitle(text);
        } catch (IllegalArgumentException e) {
            return Language.ofText(text);
        }
    }
}
