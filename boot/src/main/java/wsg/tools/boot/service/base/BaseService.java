package wsg.tools.boot.service.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import wsg.tools.boot.pojo.base.BaseDto;
import wsg.tools.boot.pojo.base.BaseEntity;

import java.util.List;

/**
 * Base interface of service including common and customized CRUD operations.
 *
 * @author Kingen
 * @since 2020/7/12
 */
public interface BaseService<D extends BaseDto, E extends BaseEntity, ID> {

    /**
     * Find all records with page limit
     *
     * @param pageable page info
     * @return records wrapped by page
     */
    Page<D> findAll(Pageable pageable);

    /**
     * Find one record based on specified condition
     *
     * @param spec condition
     * @return the record
     */
    D findOne(Specification<E> spec);

    /**
     * Find one record by id
     *
     * @param id id
     * @return the record
     */
    D findById(ID id);

    /**
     * Find all records
     *
     * @return list of all records
     */
    List<D> findAll();

    /**
     * Save a record. Update the record if id exists
     *
     * @param d record to save
     * @return saved record
     */
    D save(D d);
}
