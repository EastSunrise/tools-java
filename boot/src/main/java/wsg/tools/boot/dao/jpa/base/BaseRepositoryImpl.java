package wsg.tools.boot.dao.jpa.base;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import wsg.tools.boot.common.BeanUtilExt;
import wsg.tools.boot.pojo.base.BaseEntity;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implementation of extended {@link Repository} operations.
 *
 * @author Kingen
 * @since 2020/7/15
 */
@Transactional(readOnly = true)
public class BaseRepositoryImpl<E extends BaseEntity, ID> extends SimpleJpaRepository<E, ID> implements BaseRepository<E, ID> {

    private final JpaEntityInformation<E, ID> info;
    private final EntityManager manager;

    public BaseRepositoryImpl(JpaEntityInformation<E, ID> info, EntityManager entityManager) {
        super(info, entityManager);
        this.info = info;
        this.manager = entityManager;
        SingularAttribute<? super E, ?> idAttribute = info.getIdAttribute();
        Objects.requireNonNull(idAttribute);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public E insert(E entity) {
        if (getSource(entity, null).isEmpty()) {
            manager.persist(entity);
            return entity;
        }
        throw new IllegalArgumentException("Can't insert an existed entity.");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public E insertIgnore(E entity, Supplier<Optional<E>> supplier) {
        if (getSource(entity, supplier).isEmpty()) {
            manager.persist(entity);
            return entity;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public E updateById(E entity) {
        Optional<E> optional = getSource(entity, null);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Can't update a new entity.");
        }
        BeanUtilExt.copyPropertiesExceptNull(entity, optional.get(), false, true);
        return manager.merge(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public E updateOrInsert(E entity, Supplier<Optional<E>> supplier) {
        Optional<E> optional = getSource(entity, supplier);
        if (optional.isEmpty()) {
            manager.persist(entity);
            return entity;
        }
        BeanUtilExt.copyPropertiesExceptNull(entity, optional.get(), false, true);
        return manager.merge(entity);
    }

    private Optional<E> getSource(E entity, Supplier<Optional<E>> supplier) {
        ID id = info.getId(entity);
        Optional<E> one = Optional.empty();
        if (id != null) {
            one = findById(id);
        } else if (supplier != null) {
            one = supplier.get();
        }
        return one;
    }
}
