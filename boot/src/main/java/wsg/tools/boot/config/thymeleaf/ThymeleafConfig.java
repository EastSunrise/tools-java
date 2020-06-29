package wsg.tools.boot.config.thymeleaf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wsg.tools.boot.config.thymeleaf.dialect.CustomDialect;

/**
 * Configuration for Thymeleaf template
 *
 * @author Kingen
 * @since 2020/6/23
 */
@Configuration
public class ThymeleafConfig {

    @Bean
    @ConditionalOnMissingBean
    public CustomDialect customDialect() {
        return new CustomDialect();
    }
}
