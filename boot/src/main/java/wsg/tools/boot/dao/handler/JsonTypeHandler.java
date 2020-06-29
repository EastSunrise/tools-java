package wsg.tools.boot.dao.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.ibatis.type.MappedTypes;
import wsg.tools.common.jackson.config.BaseJacksonConfig;
import wsg.tools.common.jackson.deserializer.impl.DurationDeserializer;
import wsg.tools.common.jackson.serializer.DurationSerializer;
import wsg.tools.internet.video.enums.Country;
import wsg.tools.internet.video.enums.Language;
import wsg.tools.internet.video.jackson.deserializer.CountryDeserializer;
import wsg.tools.internet.video.jackson.deserializer.LanguageDeserializer;
import wsg.tools.internet.video.jackson.serializer.AbstractLocaleSerializer;

import java.time.Duration;
import java.util.*;

/**
 * Type Handler to serialize scalar types, like {@link Collection} and {@link Map} with Jackson.
 *
 * @author Kingen
 * @since 2020/6/27
 */
@MappedTypes({
        List.class, Map.class, ArrayList.class, Set.class, HashSet.class, HashMap.class
})
public class JsonTypeHandler extends JacksonTypeHandler {

    static {
        JacksonTypeHandler.setObjectMapper(BaseJacksonConfig.objectMapper().registerModule(new SimpleModule()
                .addSerializer(Language.class, AbstractLocaleSerializer.LANGUAGE)
                .addDeserializer(Language.class, new LanguageDeserializer())
                .addSerializer(Country.class, AbstractLocaleSerializer.COUNTRY)
                .addDeserializer(Country.class, new CountryDeserializer())
                .addSerializer(Duration.class, new DurationSerializer())
                .addDeserializer(Duration.class, new DurationDeserializer())
        ));
    }

    public JsonTypeHandler(Class<?> type) {
        super(type);
    }
}
