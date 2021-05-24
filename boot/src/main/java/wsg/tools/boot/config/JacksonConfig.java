package wsg.tools.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wsg.tools.common.constant.Constants;

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
            .registerModule(
                new JavaTimeModule()
                    .addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE))
                    .addSerializer(new LocalDateTimeSerializer(Constants.YYYY_MM_DD_HH_MM_SS)));
    }
}
