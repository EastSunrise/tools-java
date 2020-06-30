package wsg.tools.internet.video.site;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import wsg.tools.common.jackson.deserializer.impl.EnumAkaStringDeserializer;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.video.enums.Country;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.Language;
import wsg.tools.internet.video.enums.SubtypeEnum;
import wsg.tools.internet.video.jackson.deserializer.CountryExtDeserializer;
import wsg.tools.internet.video.jackson.deserializer.LanguageExtDeserializer;

/**
 * Abstract class for basis of video sites
 *
 * @author Kingen
 * @since 2020/6/20
 */
public abstract class AbstractVideoSite extends BaseSite {

    public AbstractVideoSite(String name, String domain) {
        super(name, domain);
    }

    public AbstractVideoSite(String name, String domain, double permitsPerSecond) {
        super(name, domain, permitsPerSecond);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = super.getObjectMapper();
        mapper.registerModule(new SimpleModule()
                .addDeserializer(Language.class, new LanguageExtDeserializer())
                .addDeserializer(Country.class, new CountryExtDeserializer())
                .addDeserializer(GenreEnum.class, EnumAkaStringDeserializer.of(GenreEnum.class))
                .addDeserializer(SubtypeEnum.class, EnumAkaStringDeserializer.of(SubtypeEnum.class))
        );
        return mapper;
    }
}
