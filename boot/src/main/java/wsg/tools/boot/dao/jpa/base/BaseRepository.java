package wsg.tools.boot.dao.jpa.base;

import java.util.Optional;
import java.util.function.Supplier;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;
import wsg.tools.boot.pojo.entity.base.BaseEntity;

/**
 * Base repository including common and customized CRUD operations.
 *
 * @author Kingen
 * @since 2020/7/12
 */
@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity, ID> extends
    JpaRepositoryImplementation<E, ID> {

    /**
     * Insert a new entity.
     *
     * @param entity entity to insert
     * @return entity inserted
     * @throws EntityExistsException if the entity exists.
     */
    <S extends E> S insert(S entity);

    /**
     * Update an entity by {@link ID}.
     *
     * @param entity entity to update
     * @return updated entity
     * @throws IllegalArgumentException if the given entity doesn't contain id or not exist
     */
    <S extends E> S updateById(S entity);

    /**
     * Update an entity by the given supplier.
     *
     * @param entity   entity to update
     * @param supplier supply the source entity
     * @return updated entity
     * @throws EntityNotFoundException  if can't find an entity by the supplier or id of the given
     *                                  entity
     * @throws IllegalArgumentException if the entity found by the supplier differs from the given
     *                                  one
     */
    <S extends E> S updateBy(S entity, Supplier<Optional<E>> supplier);

    /**
     * Update the entity if found by the supplier or id of the given entity Insert the given entity
     * if not found.
     *
     * @param entity   object to update or save
     * @param supplier supplier to supply source entity
     * @return updated entity with flag of inserting or updating
     * @throws IllegalArgumentException if the entity found by the supplier differs from the given
     *                                  one
     */
    <S extends E> InsertOrUpdate<S> updateOrInsert(S entity,
        @Nullable Supplier<Optional<E>> supplier);
}
