package wsg.tools.boot.dao.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.jackson.serializer.CodeSerializer;
import wsg.tools.internet.video.enums.LanguageEnum;

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
    public static class LanguageListJsonConverter extends AbstractContainerJsonConverter<List<LanguageEnum>> {
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

    static abstract class AbstractContainerJsonConverter<Container> extends BaseNonNullConverter<Container, String> {

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule())
                .registerModule(new SimpleModule()
                        .addSerializer(CodeSerializer.getInstance(String.class, LanguageEnum.class))
                        .addDeserializer(LanguageEnum.class, EnumDeserializers.getCodeDeserializer(String.class, LanguageEnum.class))
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
                throw new AppException(e);
            }
        }

        @Override
        protected String serialize(Container attribute) {
            try {
                return OBJECT_MAPPER.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new AppException(e);
            }
        }
    }
}
