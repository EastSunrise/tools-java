package wsg.tools.internet.video.jackson;

import wsg.tools.common.jackson.CommonModule;
import wsg.tools.common.jackson.deserializer.NumberDeserializersExt;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.RatedEnum;
import wsg.tools.internet.video.enums.SubtypeEnum;
import wsg.tools.internet.video.jackson.deserializer.CountryAkaDeserializer;
import wsg.tools.internet.video.jackson.deserializer.LanguageAkaDeserializer;

/**
 * A simple module registering video-related serializers.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class VideoModule extends CommonModule {
    public VideoModule() {
        addDeserializer(LanguageAkaDeserializer.INSTANCE);
        addDeserializer(CountryAkaDeserializer.INSTANCE);
        addStringAkaEnumDeserializer(GenreEnum.class);
        addStringAkaEnumDeserializer(SubtypeEnum.class);
        addStringAkaEnumDeserializer(RatedEnum.class);

        addDeserializer(NumberDeserializersExt.LongDeserializer.INSTANCE);
    }
}
