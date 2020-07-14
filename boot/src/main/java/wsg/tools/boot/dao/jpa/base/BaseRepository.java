package wsg.tools.boot.dao.jpa.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import wsg.tools.boot.pojo.base.BaseEntity;

/**
 * Base repository including common and customized CRUD operations.
 *
 * @author Kingen
 * @since 2020/7/12
 */
public interface BaseRepository<E extends BaseEntity, ID> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {
//
//    /**
//     * Update a record with specified condition. Insert the record if not exist.
//     *
//     * @param t             object to update or save
//     * @param specification condition
//     * @return result
//     */
//    boolean updateOrSave(T t, Specification<T> specification);
}
