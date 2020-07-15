package wsg.tools.boot.dao.jpa.base;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;
import wsg.tools.boot.pojo.base.BaseEntity;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base repository including common and customized CRUD operations.
 *
 * @author Kingen
 * @since 2020/7/12
 */
@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity, ID> extends JpaRepositoryImplementation<E, ID> {

    /**
     * Insert a new entity.
     *
     * @param entity entity to insert
     * @return entity inserted
     * @throws IllegalArgumentException if the entity exists.
     */
    E insert(E entity);

    /**
     * Update an entity by {@link ID}.
     *
     * @param entity entity to update
     * @return updated entity
     */
    E updateById(E entity);

    /**
     * Update a entity by {@link ID}.
     * If without {@link ID}, obtain source entity with {@link Supplier<Optional>}.
     * Update the record if source entity supplied.
     * Insert the record if not supplied.
     *
     * @param entity   object to update or save
     * @param supplier supplier to supply source entity
     * @return result
     */
    E updateOrInsert(E entity, @Nullable Supplier<Optional<E>> supplier);
}
