package wsg.tools.boot.service.base;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import wsg.tools.boot.common.util.BeanUtilExt;
import wsg.tools.boot.pojo.dto.BaseQueryDto;
import wsg.tools.boot.pojo.entity.base.BaseEntity;

/**
 * Base implementation for service, including common and customized CRUD operations.
 *
 * @author Kingen
 * @since 2020/6/24
 */
public class BaseServiceImpl {

    protected static final char ESCAPE_CHAR = '%';

    protected Predicate like(CriteriaBuilder builder, Expression<String> expression,
        String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return builder.like(expression, ESCAPE_CHAR + key + ESCAPE_CHAR, ESCAPE_CHAR);
    }

    protected <Q extends BaseQueryDto, E extends BaseEntity> Example<E> convertQuery(Q q,
        Class<E> eClass) {
        return Example.of(BeanUtilExt.convert(q, eClass));
    }

    protected <Q extends BaseQueryDto, E extends BaseEntity> Predicate getPredicate(Q q,
        Root<E> root, CriteriaBuilder builder, Class<E> eClass) {
        return QueryByExamplePredicateBuilder.getPredicate(root, builder, convertQuery(q, eClass));
    }
}
