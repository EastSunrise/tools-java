package wsg.tools.boot.dao.jpa.base;

import java.util.function.Supplier;
import wsg.tools.boot.pojo.entity.base.BaseEntity;

/**
 * Result for {@link BaseRepository#updateOrInsert(BaseEntity, Supplier)}.
 *
 * @author Kingen
 * @since 2020/7/25
 */
public final class InsertOrUpdate<E extends BaseEntity> {

    private final boolean inserted;
    private final E entity;

    private InsertOrUpdate(boolean inserted, E entity) {
        this.inserted = inserted;
        this.entity = entity;
    }

    static <E extends BaseEntity> InsertOrUpdate<E> insert(E entity) {
        return new InsertOrUpdate<>(true, entity);
    }

    static <E extends BaseEntity> InsertOrUpdate<E> update(E entity) {
        return new InsertOrUpdate<>(false, entity);
    }

    public boolean isInserted() {
        return inserted;
    }

    public boolean isUpdated() {
        return !inserted;
    }

    public E getEntity() {
        return entity;
    }
}
