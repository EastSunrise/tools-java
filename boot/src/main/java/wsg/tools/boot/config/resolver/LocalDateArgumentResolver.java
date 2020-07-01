package wsg.tools.boot.config.resolver;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Argument resolver from {@link String} to {@link LocalDate}.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Component
public class LocalDateArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return LocalDate.class.equals(methodParameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        Objects.requireNonNull(request);
        String text = request.getParameter(methodParameter.getParameter().getName());
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return LocalDate.parse(text);
    }
}
