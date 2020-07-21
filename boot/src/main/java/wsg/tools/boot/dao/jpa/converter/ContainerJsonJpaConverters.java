package wsg.tools.boot.dao.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import wsg.tools.boot.config.serializer.DurationSerializerExt;
import wsg.tools.common.jackson.CommonModule;
import wsg.tools.internet.video.enums.Language;
import wsg.tools.internet.video.jackson.deserializer.LanguageCodeDeserializer;

import javax.persistence.Converter;
import java.time.Duration;
import java.util.List;

/**
 * Converters for container types like {@link List}.
 * <p>
 * Database store data in the form of json string and converted with Jackson.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class ContainerJsonJpaConverters {

    @Converter(autoApply = true)
    public static class StringListJsonConverter extends AbstractContainerJsonConverter<List<String>> {
        public StringListJsonConverter() {
            super(new TypeReference<>() {});
        }
    }

    @Converter(autoApply = true)
    public static class LanguageListJsonConverter extends AbstractContainerJsonConverter<List<Language>> {
        public LanguageListJsonConverter() {
            super(new TypeReference<>() {});
        }
    }

    @Converter(autoApply = true)
    public static class DurationListJsonConverter extends AbstractContainerJsonConverter<List<Duration>> {
        public DurationListJsonConverter() {
            super(new TypeReference<>() {});
        }
    }

    static class AbstractContainerJsonConverter<Container> extends BaseNonNullConverter<Container, String> {

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
                .registerModule(new JavaTimeModule()
                        .addSerializer(DurationSerializerExt.INSTANCE)
                )
                .registerModule(new CommonModule()
                        .addCodeSerializer(String.class, Language.class)
                        .addDeserializer(Language.class, LanguageCodeDeserializer.INSTANCE)
                );
        private final TypeReference<Container> type;

        public AbstractContainerJsonConverter(TypeReference<Container> type) {
            this.type = type;
        }

        @Override
        protected Container deserialize(String dbData) {
            try {
                return OBJECT_MAPPER.readValue(dbData, type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected String serialize(Container attribute) {
            try {
                return OBJECT_MAPPER.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
