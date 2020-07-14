package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.jackson.deserializer.AbstractNonNullDeserializer;
import wsg.tools.internet.video.enums.Language;

/**
 * Deserialize a code to an object of {@link Language}.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class LanguageCodeDeserializer extends AbstractNonNullDeserializer<Language, String> {

    public static final LanguageCodeDeserializer INSTANCE = new LanguageCodeDeserializer();

    protected LanguageCodeDeserializer() {
        super(Language.class, String.class);
    }

    @Override
    public Language apply(String code) {
        return Language.ofCode(code);
    }
}
