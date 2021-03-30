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
     * Inserts a new entity.
     *
     * @param entity entity to be inserted
     * @return entity inserted
     * @throws EntityExistsException if the entity already exists
     */
    <S extends E> S insert(S entity);

    /**
     * Updates an entity by {@link ID}. Only not-null properties are updated.
     *
     * @param entity entity to be update
     * @return updated entity
     * @throws NullPointerException    if the given entity is {@literal null}
     * @throws EntityNotFoundException if the given entity is not found
     */
    <S extends E> S updateByIdExceptNull(S entity);

    /**
     * Updates an entity by {@link ID}. All properties are updated even null.
     *
     * @param entity entity to be update
     * @return updated entity
     * @throws NullPointerException    if the given entity is {@literal null}
     * @throws EntityNotFoundException if the given entity is not found
     */
    <S extends E> S updateById(S entity);

    /**
     * Updates the entity if found by the supplier or id of the given entity or inserts the given
     * entity if not.
     *
     * @param entity   entity to be update or inserted
     * @param supplier function to retrieve source entity
     * @return updated entity with flag of inserting or updating
     * @throws IllegalArgumentException if the entity found by the supplier differs from the given
     *                                  one
     */
    <S extends E>
    InsertOrUpdate<S> updateOrInsert(S entity, @Nullable Supplier<Optional<E>> supplier);
}
