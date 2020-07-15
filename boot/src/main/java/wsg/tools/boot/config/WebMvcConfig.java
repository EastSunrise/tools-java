package wsg.tools.boot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.format.FormatterRegistry;
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

    private StringToEnumIgnoreCaseConverterFactory converterFactory;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(converterFactory);
        Jsr310Converters.getConvertersToRegister().forEach(registry::addConverter);
    }

    @Autowired
    public void setConverterFactory(StringToEnumIgnoreCaseConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }
}
