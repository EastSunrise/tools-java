package wsg.tools.boot.config.thymeleaf.dialect;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;
import wsg.tools.boot.config.thymeleaf.expression.Customs;

import java.util.Set;

/**
 * Customized implement of {@link IExpressionObjectFactory}
 *
 * @author Kingen
 * @since 2020/6/25
 */
public class CustomExpressionFactory implements IExpressionObjectFactory {

    private static final String CUSTOM_EVALUATION_VARIABLE_NAME = "customs";

    private static final Set<String> ALL_EXPRESSION_OBJECT_NAMES = Set.of(CUSTOM_EVALUATION_VARIABLE_NAME);

    @Override
    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (CUSTOM_EVALUATION_VARIABLE_NAME.equals(expressionObjectName)) {
            return new Customs();
        }
        return null;
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return CUSTOM_EVALUATION_VARIABLE_NAME.equals(expressionObjectName);
    }
}
