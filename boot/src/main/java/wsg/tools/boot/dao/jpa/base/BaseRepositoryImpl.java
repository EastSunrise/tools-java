package wsg.tools.boot.dao.jpa.base;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import wsg.tools.boot.common.BeanUtilExt;
import wsg.tools.boot.pojo.base.BaseEntity;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
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
        Objects.requireNonNull(idAttribute, "Can't find an id attribute of entity " + info.getEntityName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends E> S insert(S entity) throws EntityExistsException {
        manager.persist(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends E> S updateById(S entity) {
        ID id = info.getId(entity);
        if (info.isNew(entity) || id == null) {
            throw new EntityNotFoundException("The entity doesn't exist.");
        }
        Optional<E> optional = findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Can't update a entity which doesn't exist.");
        }
        BeanUtilExt.copyPropertiesExceptNull(entity, optional.get(), false, true);
        return manager.merge(entity);
    }

    @Override
    public <S extends E> S updateBy(S entity, Supplier<Optional<E>> supplier) {
        ID id = info.getId(entity);
        Optional<E> optional = supplier == null ? Optional.empty() : supplier.get();
        if (optional.isEmpty()) {
            if (id == null || (optional = findById(id)).isEmpty()) {
                throw new EntityNotFoundException("Can't find an entity to update.");
            }
        }
        if (id != null && info.getId(optional.get()) != id) {
            throw new IllegalArgumentException("Supplied entity differs from the given one.");
        }
        BeanUtilExt.copyPropertiesExceptNull(entity, optional.get(), false, true);
        return manager.merge(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends E> InsertOrUpdate<S> updateOrInsert(S entity, Supplier<Optional<E>> supplier) {
        ID id = info.getId(entity);
        Optional<E> optional = supplier == null ? Optional.empty() : supplier.get();
        if (optional.isEmpty()) {
            if (id == null || (optional = findById(id)).isEmpty()) {
                manager.persist(entity);
                return InsertOrUpdate.insert(entity);
            }
        }
        if (id != null && info.getId(optional.get()) != id) {
            throw new IllegalArgumentException("Supplied entity differs from the given one.");
        }
        BeanUtilExt.copyPropertiesExceptNull(entity, optional.get(), false, true);
        return InsertOrUpdate.update(manager.merge(entity));
    }
}
