package wsg.tools.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.StatusEnum;
import wsg.tools.boot.pojo.enums.SubtypeEnum;
import wsg.tools.common.jackson.CodeModule;
import wsg.tools.internet.video.enums.Language;

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
                .registerModule(new CodeModule()
                        .addTitleSerializer(StatusEnum.class)
                        .addTitleSerializer(SubtypeEnum.class)
                        .addTitleSerializer(Language.class)
                        .addTitleSerializer(ArchivedEnum.class)
                        .addTitleSerializer(Language.class)
                )
                .registerModule(new JavaTimeModule());
    }
}
