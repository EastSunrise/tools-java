package wsg.tools.boot.service.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Base implementation for service, including common and customized CRUD operations.
 *
 * @author Kingen
 * @since 2020/6/24
 */
public class BaseServiceImpl<D extends BaseDto, E extends BaseEntity, ID> implements BaseService<D, E, ID> {

    private BaseRepository<E, ID> repository;

    private final Class<D> dClass;
    private final Class<E> eClass;

    @SuppressWarnings("unchecked")
    public BaseServiceImpl() {
        Type type = getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        dClass = (Class<D>) types[0];
        eClass = (Class<E>) types[1];
    }

    @Override
    public Optional<D> findById(ID id) {
        return repository.findById(id).map(this::convertEntity);
    }

    @Override
    public Optional<D> findOne(Specification<E> spec) {
        return repository.findOne(spec).map(this::convertEntity);
    }

    @Override
    public List<D> findAll() {
        return convertEntities(repository.findAll());
    }

    @Override
    public <Q extends BaseQueryDto> List<D> findAll(Q q) {
        return findAll(convertQuery(q));
    }

    @Override
    public List<D> findAll(Example<E> example) {
        return convertEntities(repository.findAll(example));
    }

    @Override
    public List<D> findAll(Specification<E> spec) {
        return convertEntities(repository.findAll(spec));
    }

    @Override
    public Page<D> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::convertEntity);
    }

    @Override
    public <Q extends BaseQueryDto> Page<D> findAll(Q q, Pageable pageable) {
        return findAll(convertQuery(q), pageable);
    }

    @Override
    public Page<D> findAll(Example<E> example, Pageable pageable) {
        return repository.findAll(example, pageable).map(this::convertEntity);
    }

    @Override
    public Page<D> findAll(Specification<E> spec, Pageable pageable) {
        return repository.findAll(spec, pageable).map(this::convertEntity);
    }

    @Override
    public List<D> findAllById(Iterable<ID> ids) {
        return convertEntities(repository.findAllById(ids));
    }

    @Override
    public long count(Specification<E> spec) {
        return repository.count(spec);
    }

    @Override
    public D insert(D record) {
        return convertEntity(repository.insert(convertDto(record)));
    }

    @Override
    public D updateById(D record) {
        return convertEntity(repository.updateById(convertDto(record)));
    }

    @Override
    public D updateOrInsert(D source, Supplier<Optional<E>> supplier) {
        return convertEntity(repository.updateOrInsert(convertDto(source), supplier));
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
