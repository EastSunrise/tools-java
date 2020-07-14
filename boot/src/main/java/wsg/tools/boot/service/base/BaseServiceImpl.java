package wsg.tools.boot.service.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import wsg.tools.boot.common.util.BeanUtilExt;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.base.BaseDto;
import wsg.tools.boot.pojo.base.BaseEntity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Base implementation for service, including common and customized CRUD operations.
 *
 * @author Kingen
 * @since 2020/6/24
 */
public class BaseServiceImpl<D extends BaseDto, E extends BaseEntity, ID> implements BaseService<D, E, ID> {

    private BaseRepository<E, ID> repository;

    private Class<D> dClass;
    private Class<E> eClass;
    private Class<ID> idClass;

    @SuppressWarnings("unchecked")
    public BaseServiceImpl() {
        Type type = getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        dClass = (Class<D>) types[0];
        eClass = (Class<E>) types[1];
        idClass = (Class<ID>) types[2];
    }

    @Override
    public Page<D> findAll(Pageable pageable) {
        Page<E> all = repository.findAll(pageable);
        return all.map(this::convertToDto);
    }

    @Override
    public D findOne(Specification<E> spec) {
        Optional<E> one = repository.findOne(spec);
        if (one.isEmpty()) {
            return null;
        }
        return convertToDto(one.get());
    }

    @Override
    public D findById(ID id) {
        Optional<E> optional = repository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        return convertToDto(optional.get());
    }

    @Override
    public List<D> findAll() {
        return repository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public D save(D d) {
        E e = convertToEntity(d);
        e = repository.save(e);
        return convertToDto(e);
    }

    protected D convertToDto(E e) {
        return BeanUtilExt.convert(e, dClass);
    }

    protected E convertToEntity(D d) {
        return BeanUtilExt.convert(d, eClass);
    }

    @Autowired
    public void setRepository(BaseRepository<E, ID> repository) {
        this.repository = repository;
    }
}
