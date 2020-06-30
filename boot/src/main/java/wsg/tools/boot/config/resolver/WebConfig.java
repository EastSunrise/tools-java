package wsg.tools.boot.config.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration for Web MVC.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private LocalDateArgumentResolver localDateArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(localDateArgumentResolver);
    }

    @Autowired
    public void setLocalDateArgumentResolver(LocalDateArgumentResolver localDateArgumentResolver) {
        this.localDateArgumentResolver = localDateArgumentResolver;
    }
}
