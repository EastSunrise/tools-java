package wsg.tools.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wsg.tools.common.jackson.config.BaseJacksonConfig;
import wsg.tools.internet.video.enums.Country;
import wsg.tools.internet.video.enums.Language;
import wsg.tools.internet.video.jackson.deserializer.CountryDeserializer;
import wsg.tools.internet.video.jackson.deserializer.LanguageDeserializer;
import wsg.tools.internet.video.jackson.serializer.AbstractLocaleSerializer;

/**
 * Configurations for Jackson.
 *
 * @author Kingen
 * @since 2020/7/10
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = BaseJacksonConfig.createObjectMapper();
        mapper.registerModule(new SimpleModule()
                .addSerializer(Language.class, AbstractLocaleSerializer.LANGUAGE)
                .addSerializer(Country.class, AbstractLocaleSerializer.COUNTRY)
                .addDeserializer(Language.class, new LanguageDeserializer())
                .addDeserializer(Country.class, new CountryDeserializer())
        );
        return mapper;
    }
}
