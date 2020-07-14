package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.AbstractNonNullDeserializer;
import wsg.tools.internet.video.enums.Language;

/**
 * Deserialize a Chinese title or an English text to {@link Language}.
 *
 * @author Kingen
 * @since 2020/6/20
 */
public class LanguageAkaDeserializer extends AbstractNonNullDeserializer<Language, String> {

    public static final LanguageAkaDeserializer INSTANCE = new LanguageAkaDeserializer();

    protected LanguageAkaDeserializer() {
        super(Language.class, String.class);
    }

    @Override
    public Language apply(String aka) {
        try {
            return Language.ofTitle(aka);
        } catch (IllegalArgumentException e) {
            return Language.ofText(aka);
        }
    }
}
