package wsg.tools.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.TypeEnum;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.serializer.EnumSerializers;
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
                .registerModule(new SimpleModule()
                        .addSerializer(EnumSerializers.getTitleSerializer(MarkEnum.class))
                        .addSerializer(EnumSerializers.getTitleSerializer(ArchivedEnum.class))
                        .addSerializer(EnumSerializers.getTitleSerializer(TypeEnum.class))
                )
                .registerModule(new JavaTimeModule()
                        .addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE))
                        .addSerializer(new LocalDateTimeSerializer(Constants.STANDARD_DATE_TIME_FORMATTER))
                );
    }
}
