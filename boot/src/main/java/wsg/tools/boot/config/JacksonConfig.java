package wsg.tools.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.serializer.TextSerializer;
import wsg.tools.common.jackson.serializer.TitleSerializer;
import wsg.tools.internet.movie.common.enums.MarkEnum;

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
                .registerModule(new SimpleModule()
                        .addSerializer(TitleSerializer.getInstance(MarkEnum.class))
                        .addSerializer(TextSerializer.getInstance(VideoStatus.class))
                )
                .registerModule(new JavaTimeModule()
                        .addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE))
                        .addSerializer(new LocalDateTimeSerializer(Constants.STANDARD_DATE_TIME_FORMATTER))
                );
    }
}
