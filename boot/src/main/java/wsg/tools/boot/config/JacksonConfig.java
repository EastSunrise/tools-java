package wsg.tools.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wsg.tools.boot.config.serializer.DurationSerializerExt;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.CommonModule;
import wsg.tools.internet.video.enums.Language;
import wsg.tools.internet.video.enums.MarkEnum;

import java.time.format.DateTimeFormatter;

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
        return new ObjectMapper()
                .registerModule(new CommonModule()
                        .addTitleSerializer(MarkEnum.class)
                        .addTitleSerializer(Language.class)
                        .addTitleSerializer(ArchivedEnum.class)
                        .addTitleSerializer(Language.class)
                )
                .registerModule(new JavaTimeModule()
                        .addSerializer(DurationSerializerExt.INSTANCE)
                        .addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE))
                        .addSerializer(new LocalDateTimeSerializer(Constants.STANDARD_DATE_TIME_FORMATTER))
                );
    }
}
