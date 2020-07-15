package wsg.tools.boot.service.base;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import wsg.tools.boot.pojo.base.BaseDto;
import wsg.tools.boot.pojo.base.BaseEntity;
import wsg.tools.boot.pojo.base.BaseQueryDto;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base interface of service including common and customized CRUD operations.
 *
 * @author Kingen
 * @since 2020/7/12
 */
public interface BaseService<D extends BaseDto, E extends BaseEntity, ID> {

    /**
     * Retrieves a record by its id.
     *
     * @param id must not be {@literal null}.
     * @return the record with the given id or {@literal Optional#empty()} if none found.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    Optional<D> findById(ID id);

    /**
     * Returns a single record matching the given {@link Specification} or {@link Optional#empty()} if none found.
     *
     * @param spec can be {@literal null}.
     * @return never {@literal null}.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if more than one entity found.
     */
    Optional<D> findOne(@Nullable Specification<E> spec);

    /**
     * Returns all instances of the type.
     *
     * @return all records
     */
    List<D> findAll();

    /**
     * Returns all records matching the given condition.
     *
     * @param q query condition
     * @return all records matched
     */
    <Q extends BaseQueryDto> List<D> findAll(Q q);

    /**
     * Returns all records matching the given {@link Example}. In case no match could be found an empty {@link Iterable}
     * is returned.
     *
     * @param example must not be {@literal null}.
     * @return all records matching the given {@link Example}.
     */
    List<D> findAll(Example<E> example);

    /**
     * Returns all records matching the given {@link Specification}.
     *
     * @param spec can be {@literal null}.
     * @return never {@literal null}.
     */
    List<D> findAll(@Nullable Specification<E> spec);

    /**
     * Returns a {@link Page} of records meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable pagination
     * @return a page of records
     */
    Page<D> findAll(Pageable pageable);

    /**
     * Returns a {@link Page} of records matching the given condition
     *
     * @param q        condition
     * @param pageable can be {@literal null}.
     * @return a {@link Page} of records
     */
    <Q extends BaseQueryDto> Page<D> findAll(Q q, Pageable pageable);

    /**
     * Returns a {@link Page} of records matching the given {@link Example}. In case no match could be found, an empty
     * {@link Page} is returned.
     *
     * @param example  must not be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return a {@link Page} of records matching the given {@link Example}.
     */
    Page<D> findAll(Example<E> example, Pageable pageable);

    /**
     * Returns a {@link Page} of records matching the given {@link Specification}.
     *
     * @param spec     can be {@literal null}.
     * @param pageable must not be {@literal null}.
     * @return never {@literal null}.
     */
    Page<D> findAll(@Nullable Specification<E> spec, Pageable pageable);

    /**
     * Returns all instances of the type {@code D} with the given IDs.
     * <p>
     * If some or all ids are not found, no entities are returned for these IDs.
     * <p>
     * Note that the order of elements in the result is not guaranteed.
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
     * {@literal ids}.
     * @throws IllegalArgumentException in case the given {@link Iterable ids} or one of its items is {@literal null}.
     */
    List<D> findAllById(Iterable<ID> ids);

    /**
     * Returns the number of instances that the given {@link Specification} will return.
     *
     * @param spec the {@link Specification} to count instances for. Can be {@literal null}.
     * @return the number of instances.
     */
    long count(@Nullable Specification<E> spec);

    /**
     * Insert a new record.
     *
     * @param record record to insert
     * @return inserted record
     * @throws IllegalArgumentException if the record exists.
     */
    D insert(D record);

    /**
     * Update a record by {@link ID}.
     *
     * @param record record to update
     * @return updated record
     */
    D updateById(D record);

    /**
     * Update a record by {@link ID}.
     * If without {@link ID}, query the record with {@link Supplier<Optional>}.
     * Update the record if exists.
     * Insert the record if not exist.
     *
     * @param record   record to update or save
     * @param supplier supplier to supply source entity.
     * @return result
     */
    D updateOrInsert(D record, Supplier<Optional<E>> supplier);
}