package wsg.tools.boot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.format.FormatterRegistry;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wsg.tools.boot.config.converter.StringToEnumIgnoreCaseConverterFactory;

/**
 * Configuration fro web mvc.
 *
 * @author Kingen
 * @since 2020/7/14
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final StringToEnumIgnoreCaseConverterFactory converterFactory;

    @Autowired
    public WebMvcConfig(StringToEnumIgnoreCaseConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(converterFactory);
        Jsr310Converters.getConvertersToRegister().forEach(registry::addConverter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }

    @Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter();
    }
}
