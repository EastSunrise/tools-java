package wsg.tools.boot.config.thymeleaf.dialect;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

/**
 * Customized Thymeleaf Dialect.
 *
 * @author Kingen
 * @since 2020/6/25
 */
public class CustomDialect extends AbstractDialect implements IExpressionObjectDialect {

    private final IExpressionObjectFactory CUSTOM_EXPRESSION_OBJECTS_FACTORY = new CustomExpressionFactory();

    public CustomDialect() {
        super("custom");
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return CUSTOM_EXPRESSION_OBJECTS_FACTORY;
    }

}
