package wsg.tools.boot.service.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import wsg.tools.boot.common.BeanUtilExt;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.base.BaseDto;
import wsg.tools.boot.pojo.base.BaseEntity;
import wsg.tools.boot.pojo.base.BaseQueryDto;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base implementation for service, including common and customized CRUD operations.
 *
 * @author Kingen
 * @since 2020/6/24
 */
public class BaseServiceImpl<D extends BaseDto, E extends BaseEntity, ID> {

    private final Class<D> dClass;
    private final Class<E> eClass;
    private BaseRepository<E, ID> repository;

    @SuppressWarnings("unchecked")
    protected BaseServiceImpl() {
        Type type = getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        dClass = (Class<D>) types[0];
        eClass = (Class<E>) types[1];
    }

    public List<D> findAll(Specification<E> spec) {
        return convertEntities(repository.findAll(spec));
    }

    protected <Q extends BaseQueryDto> Example<E> convertQuery(Q q) {
        return Example.of(BeanUtilExt.convert(q, eClass));
    }

    protected <Q extends BaseQueryDto> Predicate getPredicate(Q q, Root<E> root, CriteriaBuilder builder) {
        return QueryByExamplePredicateBuilder.getPredicate(root, builder, convertQuery(q));
    }

    protected D convertEntity(E e) {
        return BeanUtilExt.convert(e, dClass);
    }

    protected E convertDto(D d) {
        return BeanUtilExt.convert(d, eClass);
    }

    protected List<D> convertEntities(List<E> es) {
        return es.stream().map(this::convertEntity).collect(Collectors.toList());
    }

    @Autowired
    public void setRepository(BaseRepository<E, ID> repository) {
        this.repository = repository;
    }
}
